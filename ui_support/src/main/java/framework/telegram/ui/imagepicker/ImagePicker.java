package framework.telegram.ui.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import framework.telegram.ui.imagepicker.internal.entity.MediaInfo;
import framework.telegram.ui.imagepicker.ui.ImagePickerActivity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

/**
 * Entry for ImagePicker's media selection.
 */
public final class ImagePicker {

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    private ImagePicker(AppCompatActivity activity) {
        this(activity, null);
    }

    private ImagePicker(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private ImagePicker(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * Start ImagePicker from an Activity.
     * <p>
     * This Activity's {@link AppCompatActivity#onActivityResult(int, int, Intent)} will be called when user
     * finishes selecting.
     *
     * @param activity Activity instance.
     * @return ImagePicker instance.
     */
    public static ImagePicker from(AppCompatActivity activity) {
        return new ImagePicker(activity);
    }

    /**
     * Start ImagePicker from a Fragment.
     * <p>
     * This Fragment's {@link Fragment#onActivityResult(int, int, Intent)} will be called when user
     * finishes selecting.
     *
     * @param fragment Fragment instance.
     * @return ImagePicker instance.
     */
    public static ImagePicker from(Fragment fragment) {
        return new ImagePicker(fragment);
    }

    /**
     * Obtain user selected media' {@link Uri} list in the starting Activity or Fragment.
     *
     * @param data Intent passed by {@link Activity#onActivityResult(int, int, Intent)} or
     *             {@link Fragment#onActivityResult(int, int, Intent)}.
     * @return User selected media' {@link Uri} list.
     */
    public static List<Uri> obtainResult(Intent data) {
        return data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_RESULT_SELECTION);
    }

    /**
     * Obtain user selected media path list in the starting Activity or Fragment.
     *
     * @param data Intent passed by {@link Activity#onActivityResult(int, int, Intent)} or
     *             {@link Fragment#onActivityResult(int, int, Intent)}.
     * @return User selected media path list.
     */
    public static List<String> obtainPathResult(Intent data) {
        return data.getStringArrayListExtra(ImagePickerActivity.EXTRA_RESULT_SELECTION_PATH);
    }

    /**
     * Obtain user selected media info list in the starting Activity or Fragment.
     *
     * @param data Intent passed by {@link Activity#onActivityResult(int, int, Intent)} or
     *             {@link Fragment#onActivityResult(int, int, Intent)}.
     * @return User selected media path list.
     */
    public static List<MediaInfo> obtainInfoResult(Intent data) {
        return data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_RESULT_SELECTION_INFO);
    }

    /**
     * Obtain state whether user decide to use selected media in original
     *
     * @param data Intent passed by {@link Activity#onActivityResult(int, int, Intent)} or
     *             {@link Fragment#onActivityResult(int, int, Intent)}.
     * @return Whether use original photo
     */
    public static boolean obtainOriginalState(Intent data) {
        return data.getBooleanExtra(ImagePickerActivity.EXTRA_RESULT_ORIGINAL_ENABLE, false);
    }

    /**
     * MIME types the selection constrains on.
     * <p>
     * Types not included in the set will still be shown in the grid but can't be chosen.
     *
     * @param mimeTypes MIME types set user can choose from.
     * @return {@link SelectionCreator} to build select specifications.
     * @see MimeType
     * @see SelectionCreator
     */
    public SelectionCreator choose(Set<MimeType> mimeTypes) {
        return this.choose(mimeTypes, true);
    }

    /**
     * MIME types the selection constrains on.
     * <p>
     * Types not included in the set will still be shown in the grid but can't be chosen.
     *
     * @param mimeTypes          MIME types set user can choose from.
     * @param mediaTypeExclusive Whether can choose images and videos at the same time during one single choosing
     *                           process. true corresponds to not being able to choose images and videos at the same
     *                           time, and false corresponds to being able to do this.
     * @return {@link SelectionCreator} to build select specifications.
     * @see MimeType
     * @see SelectionCreator
     */
    public SelectionCreator choose(Set<MimeType> mimeTypes, boolean mediaTypeExclusive) {
        return new SelectionCreator(this, mimeTypes, mediaTypeExclusive);
    }

    @Nullable
    Activity getActivity() {
        return mContext.get();
    }

    @Nullable
    Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }

}
