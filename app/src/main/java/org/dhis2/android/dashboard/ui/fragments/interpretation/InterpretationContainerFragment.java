package org.dhis2.android.dashboard.ui.fragments.interpretation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.dhis2.android.dashboard.R;
import org.dhis2.android.dashboard.api.models.Interpretation;
import org.dhis2.android.dashboard.api.models.Interpretation$Table;
import org.dhis2.android.dashboard.api.models.meta.State;
import org.dhis2.android.dashboard.api.persistence.loaders.DbLoader;
import org.dhis2.android.dashboard.api.persistence.loaders.Query;
import org.dhis2.android.dashboard.ui.fragments.BaseFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by arazabishov on 7/24/15.
 */
public class InterpretationContainerFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Boolean> {
    private static final int LOADER_ID = 534845;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<Boolean> onCreateLoader(int i, Bundle bundle) {
        List<BaseModel.Action> actionsToTrack = Arrays.asList(
                BaseModel.Action.INSERT, BaseModel.Action.DELETE);
        List<DbLoader.TrackedTable> trackedTables = Arrays.asList(
                new DbLoader.TrackedTable(Interpretation.class, actionsToTrack));
        return new DbLoader<>(getActivity().getApplicationContext(),
                trackedTables, new InterpretationsQuery());
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean hasData) {
        if (loader != null && loader.getId() == LOADER_ID) {
            if (hasData) {
                // we don't want to attach the same fragment
                if (!isFragmentAttached(InterpretationFragment.TAG)) {
                    attachFragment(new InterpretationFragment(),
                            InterpretationFragment.TAG);
                }
            } else {
                if (!isFragmentAttached(InterpretationEmptyFragment.TAG)) {
                    attachFragment(new InterpretationEmptyFragment(),
                            InterpretationEmptyFragment.TAG);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
        // stub implementation
    }

    private void attachFragment(Fragment fragment, String tag) {
        System.out.println("ATTACH_FRAGMENT IS CALLED");
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_content_frame, fragment, tag)
                .commitAllowingStateLoss();
    }

    private boolean isFragmentAttached(String tag) {
        return getChildFragmentManager().findFragmentByTag(tag) != null;
    }

    private static class InterpretationsQuery implements Query<Boolean> {

        @Override
        public Boolean query(Context context) {
            List<Interpretation> interpretations = new Select()
                    .from(Interpretation.class)
                    .where(Condition.column(Interpretation$Table
                            .STATE).isNot(State.TO_DELETE.toString()))
                    .queryList();

            return interpretations != null && interpretations.size() > 0;
        }
    }
}
