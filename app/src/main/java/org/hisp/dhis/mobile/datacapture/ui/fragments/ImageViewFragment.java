package org.hisp.dhis.mobile.datacapture.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.utils.PicassoProvider;

import uk.co.senab.photoview.PhotoView;

public class ImageViewFragment extends BaseFragment {
    private static final String IMAGE_URL_EXTRA = "imageUrlExtra";

    private PhotoView mPhotoView;
    private Picasso mImageLoader;

    public static ImageViewFragment newInstance(String request) {
        ImageViewFragment fragment = new ImageViewFragment();
        Bundle args = new Bundle();

        args.putString(IMAGE_URL_EXTRA, request);
        fragment.setArguments(args);

        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = PicassoProvider.getInstance(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPhotoView = (PhotoView) inflater.inflate(R.layout.fragment_image_view_layout, container, false);
        return mPhotoView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().getString(IMAGE_URL_EXTRA) != null) {
            mImageLoader.load(getArguments().getString(IMAGE_URL_EXTRA))
                    .placeholder(R.drawable.stub_dashboard_background)
                    .into(mPhotoView);
        }
    }
}