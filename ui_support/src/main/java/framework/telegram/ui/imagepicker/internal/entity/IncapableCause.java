package framework.telegram.ui.imagepicker.internal.entity;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;

import framework.telegram.ui.dialog.AppDialog;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@SuppressWarnings("unused")
public class IncapableCause {
    public static final int TOAST = 0x00;
    public static final int DIALOG = 0x01;
    public static final int NONE = 0x02;

    @Retention(SOURCE)
    @IntDef({TOAST, DIALOG, NONE})
    public @interface Form {
    }

    private int mForm = TOAST;
    private String mTitle;
    private String mMessage;

    public IncapableCause(String message) {
        mMessage = message;
    }

    public IncapableCause(String title, String message) {
        mTitle = title;
        mMessage = message;
    }

    public IncapableCause(@Form int form, String message) {
        mForm = form;
        mMessage = message;
    }

    public IncapableCause(@Form int form, String title, String message) {
        mForm = form;
        mTitle = title;
        mMessage = message;
    }

    public static void handleCause(Context context, IncapableCause cause) {
        if (cause == null)
            return;

        switch (cause.mForm) {
            case NONE:
                // do nothing.
                break;
            case DIALOG:
                AppDialog.Companion.show((AppCompatActivity) context, (AppCompatActivity) context, materialDialog -> {
                    materialDialog.title(null, cause.mTitle);
                    materialDialog.message(null, cause.mMessage,null);
                    return null;
                });
                break;
            case TOAST:
            default:
                Toast.makeText(context, cause.mMessage, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
