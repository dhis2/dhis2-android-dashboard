package org.hisp.dhis.android.dashboard.ui.fragments.interpretation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.ui.fragments.BaseFragment;
import org.hisp.dhis.android.sdk.core.persistence.loaders.DbLoader;
import org.hisp.dhis.android.sdk.core.persistence.loaders.Query;
import org.hisp.dhis.android.sdk.core.persistence.loaders.TrackedTable;
import org.hisp.dhis.android.sdk.models.common.meta.DbAction;
import org.hisp.dhis.android.sdk.models.interpretation.Interpretation;

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
        List<DbAction> actionsToTrack = Arrays.asList(
                DbAction.INSERT, DbAction.DELETE);
        List<TrackedTable> trackedTables = Arrays.asList(
                new TrackedTable(Interpretation.class, actionsToTrack));
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
            /* List<Interpretation> interpretations = Models.interpretations()
                    .filter(State.TO_DELETE);
            return interpretations != null && interpretations.size() > 0; */
            return null;
        }
    }
}
