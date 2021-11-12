package com.amir.stickergram.sticker.pack.user;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.stickergram.BuildConfig;
import com.amir.stickergram.HelpActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.UserStickersActivity;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.Constants;
import com.amir.stickergram.infrastructure.Loader;
import com.amir.whatsapp.WhitelistCheck;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import kotlin.jvm.internal.Intrinsics;

public class UserIconPackDetailedFragment extends BaseFragment
        implements OnStickerClickListener, View.OnClickListener {
    private RecyclerView recyclerView;
    private PackAdapter adapter;
    private String folder;
    private ProgressBar progressBar;
    private TextView folderText;
    private Button linkButton;
    private LinearLayout publishNoteContainer;
    private boolean isInPackCreationMode;
    private MenuItem modeChooserMenuItem;
    private TextView publishNoteText;
    private boolean publishNoteIsHidden = false;
    private View whatsappButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        isInPackCreationMode = false;
        View view = inflater.inflate(R.layout.fragment_user_detailed_pack, container, false);
        setFont((ViewGroup) view);
        recyclerView = view.findViewById(R.id.fragment_user_detailed_pack_list);
        folderText = view.findViewById(R.id.fragment_user_detailed_pack_text_folder);
        linkButton = view.findViewById(R.id.fragment_user_detailed_pack_pack_creation_mode);
        whatsappButton = view.findViewById(R.id.fragment_user_detailed_pack_export_to_whatsapp);

        View publishNoteCloseButton = view.findViewById(R.id.include_detailed_note_close);
        publishNoteText = view.findViewById(R.id.include_detailed_note_text);
        View publishIcon = view.findViewById(R.id.include_detailed_note_info_icon);
        publishNoteContainer = view.findViewById(R.id.include_detailed_note_container);

        progressBar = view.findViewById(R.id.fragment_icon_detailed_progressBar);

        if (publishNoteCloseButton != null &&
                publishIcon != null &&
                publishNoteText != null &&
                linkButton != null &&
                whatsappButton != null &&
                publishNoteContainer != null) {
            publishNoteText.setOnClickListener(this);
            publishNoteText.setOnClickListener(this);
            publishNoteCloseButton.setOnClickListener(this);
            linkButton.setOnClickListener(this);
            whatsappButton.setOnClickListener(this);
            publishNoteContainer.setVisibility(View.GONE);
        }
        refresh(folder); // this guy sets the adapter
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.user_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.user_fragment_pack_creation_mode_option) {
            modeChooserMenuItem = item;
            gotoPackCreationMode(!isInPackCreationMode);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        FragmentActivity activity = getActivity();
        if (itemId == R.id.fragment_user_detailed_pack_pack_creation_mode) {
            if (isInPackCreationMode) {
                if (activity != null)
                    Loader.INSTANCE.goToBotInTelegram((BaseActivity) activity);
            } else {
                gotoPackCreationMode(true);
            }
        } else if (itemId == R.id.include_detailed_note_close) {
            if (publishNoteContainer != null) {
                publishNoteContainer.setVisibility(View.GONE);
                publishNoteIsHidden = true;
//                Log.e(getClass().getSimpleName(), "on click: " + publishNoteIsHidden);
            }
        } else if (itemId == R.id.fragment_user_detailed_pack_export_to_whatsapp) {
            if (adapter.getItemCount() < 3) {
                Toast.makeText(activity, getString(R.string.no_less_than_3), Toast.LENGTH_LONG).show();
            } else {
                new AsyncTaskAddWhatsapp().execute();
                if (adapter.getItemCount() > 30) {
                    Toast.makeText(activity, getString(R.string.more_than_30), Toast.LENGTH_LONG).show();
                }
            }
        } else if (itemId == R.id.include_detailed_note_text || itemId == R.id.include_detailed_note_info_icon) {
            if (activity != null) {
                activity.finish();
                startActivity(new Intent(activity, HelpActivity.class));
            }
        }
    }

    private void gotoPackCreationMode(boolean flag) {
//        if (publishNoteIsHidden)
//            publishNoteContainer.setVisibility(View.VISIBLE);
        if (flag) {
            if (!isInPackCreationMode) {
                publishNoteContainer.setVisibility(View.VISIBLE);
                publishNoteIsHidden = false;
            }
            whatsappButton.setVisibility(View.GONE);
            if (modeChooserMenuItem != null) {
                modeChooserMenuItem.setTitle(getString(R.string.go_to_normal_mode));
            }
            linkButton.setVisibility(View.VISIBLE);
            isInPackCreationMode = true;
            publishNoteText.setText(getString(R.string.you_are_in_pack_creation_mode));
        } else {
            if (isInPackCreationMode) {
                publishNoteContainer.setVisibility(View.VISIBLE);
                publishNoteIsHidden = false;
            }
            if (modeChooserMenuItem != null)
                modeChooserMenuItem.setTitle(getString(R.string.go_to_pack_creation_mode));
            whatsappButton.setVisibility(View.VISIBLE);
            linkButton.setVisibility(View.GONE);
            isInPackCreationMode = false;
            publishNoteText.setText(getString(R.string.to_save_permanently));
//            publishNoteContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnIconClicked(final PackItem item) {
        final BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null) {
            if (isInPackCreationMode)
                sendImageToBot(activity, item);
            else
                sendImageToUser(activity, item);
        }
    }

    private void sendImageToUser(final BaseActivity activity, final PackItem item) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    if (Loader.INSTANCE.getActivePack() != null) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setPackage(Loader.INSTANCE.getActivePack());
                        intent.setType("application/pdf");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(item.getWebpDir())));
                        startActivity(Intent.createChooser(intent, "Send to Telegram"));
                    } else {
                        Toast.makeText(activity, getString(R.string.telegram_is_not_installed_you_can_t_create_sticker), Toast.LENGTH_LONG).show();
                    }
                } else if (which == Dialog.BUTTON_NEGATIVE) {
                    saveToGallery(item, activity);
                } else if (which == Dialog.BUTTON_NEUTRAL) {
                    sendImageToBot(activity, item);
                    Toast.makeText(getContext(), getString(R.string.choose_the_stickers_bot), Toast.LENGTH_LONG).show();
                }
            }
        };

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_single_item, null, false);
        setFont((ViewGroup) view);
        TextView textView = view.findViewById(R.id.dialog_single_item_title);
        textView.setText(activity.getString(R.string.do_you_want_to_send_this_sticker));
        ImageView stickerImage = view.findViewById(R.id.dialog_single_item_image);
        if (stickerImage != null)
            stickerImage.setImageBitmap(BitmapFactory.decodeFile(item.getDir()));

        final AlertDialog sendImageToUserDialog = new AlertDialog.Builder(activity)
                .setView(view)
//                .setTitle(activity.getString(R.string.do_you_want_to_send_this_sticker))
                .setNegativeButton(activity.getString(R.string.export), listener)
                .setPositiveButton(activity.getString(R.string.send), listener)
                .setNeutralButton(activity.getString(R.string.send_to_bot), listener)
                .create();


        sendImageToUserDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
//                activity.setFont((TextView) sendToBotDialog.findViewById(android.R.id.message));
                activity.setFont(sendImageToUserDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
                activity.setFont(sendImageToUserDialog.getButton(AlertDialog.BUTTON_NEUTRAL));
                activity.setFont(sendImageToUserDialog.getButton(AlertDialog.BUTTON_POSITIVE));
            }
        });

        sendImageToUserDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                activity.setFont(sendImageToUserDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
                activity.setFont(sendImageToUserDialog.getButton(AlertDialog.BUTTON_POSITIVE));
                activity.setFont(sendImageToUserDialog.getButton(AlertDialog.BUTTON_NEUTRAL));
            }
        });

        sendImageToUserDialog.show();
    }

    private Uri saveToGallery(final PackItem item, final BaseActivity activity) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, item.getFolder() + item.getName());
        values.put(MediaStore.Images.Media.DISPLAY_NAME, item.getFolder() + item.getName());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures" + Constants.STICKERGRAM);
        }
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri url = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            InputStream in = new FileInputStream(item.getDir());
            OutputStream fo = contentResolver.openOutputStream(url);
            Loader.INSTANCE.copyFile(in, fo);
            in.close();
            fo.close();
            Toast.makeText(activity, activity.getString(R.string.export), Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }


    private void sendImageToBot(final BaseActivity activity, final PackItem item) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    if (Loader.INSTANCE.getActivePack() != null) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setPackage(Loader.INSTANCE.getActivePack());
                        intent.setType("application/pdf");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            intent.putExtra(Intent.EXTRA_STREAM, saveToGallery(item, activity));
                        } else {
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(item.getDir())));
                        }
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
//                        startActivity(intent);
                        startActivity(Intent.createChooser(intent, "Send to Telegram"));
                        Toast.makeText(getContext(), getString(R.string.choose_the_stickers_bot), Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(activity, getString(R.string.telegram_is_not_installed_you_can_t_create_sticker), Toast.LENGTH_LONG).show();
                } else if (which == Dialog.BUTTON_NEUTRAL) {
                    activity.finish();
                    startActivity(new Intent(activity, HelpActivity.class));
                }
            }
        };

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_send_sticker, null, false);
        setFont((ViewGroup) view);
        ImageView stickerImage = view.findViewById(R.id.dialog_send_sticker_image_view);

        if (BaseActivity.Companion.isInLandscape()) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.RIGHT_OF, R.id.dialog_send_sticker_note_container);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(RelativeLayout.END_OF, R.id.dialog_send_sticker_note_container);
            }
            stickerImage.setLayoutParams(params);
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams((int) (200 * BaseActivity.Companion.getDensity()), ViewGroup.LayoutParams.WRAP_CONTENT);
            View noteText = view.findViewById(R.id.dialog_send_sticker_note_container);
            if (noteText != null) noteText.setLayoutParams(params2);
        }

        Bitmap bitmap = BitmapFactory.decodeFile(item.getDir());
        if (stickerImage != null)
            stickerImage.setImageBitmap(bitmap);

        final AlertDialog sendToBotDialog = new AlertDialog.Builder(activity)
                .setView(view)
//                .setTitle(activity.getString(R.string.add_to_pack))
                .setNegativeButton(activity.getString(R.string.no), listener)
                .setPositiveButton(activity.getString(R.string.add), listener)
                .setNeutralButton(getString(R.string.help), listener)
                .create();


        sendToBotDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
//                activity.setFont((TextView) sendToBotDialog.findViewById(android.R.id.message));
                activity.setFont(sendToBotDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
                activity.setFont(sendToBotDialog.getButton(AlertDialog.BUTTON_NEUTRAL));
                activity.setFont(sendToBotDialog.getButton(AlertDialog.BUTTON_POSITIVE));
            }
        });


        sendToBotDialog.show();
    }


    @Override
    public void OnLongClicked(final PackItem item) {
        final BaseActivity activity = (BaseActivity) getActivity();
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    File file = new File(item.getDir());
                    if (file.exists()) {
                        file.delete();
                        adapter.itemRemoved(item.getDir());
                    }
                }
            }
        };
        if (activity != null) {
            View view = activity.getLayoutInflater().inflate(R.layout.dialog_single_item, null, false);
            setFont((ViewGroup) view);
            TextView textView = view.findViewById(R.id.dialog_single_item_title);
            textView.setText(activity.getString(R.string.do_you_want_to_delete_this_sticker));

            ImageView stickerImage = view.findViewById(R.id.dialog_single_item_image);

            Bitmap bitmap = BitmapFactory.decodeFile(item.getDir());
            if (stickerImage != null)
                stickerImage.setImageBitmap(bitmap);

            final AlertDialog deleteDialog = new AlertDialog.Builder(activity)
                    .setView(view)
//                .setTitle(activity.getString(R.string.do_you_want_to_delete_this_sticker))
                    .setNegativeButton(activity.getString(R.string.cancel), listener)
                    .setPositiveButton(activity.getString(R.string.delete), listener)
                    .create();

            deleteDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    activity.setFont(deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
                    activity.setFont(deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE));
                }
            });


            deleteDialog.show();
        }
    }

    @Override
    public void folderDeleted() {
        //restarting the activity without animation
        //that's the way to do it
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
            startActivity(new Intent(activity, UserStickersActivity.class));
            activity.overridePendingTransition(0, 0);
        }
    }


    public void refresh(String folder) {
        this.folder = folder;
        if (folder == null) return;

        if (folderText != null && linkButton != null) {
            folderText.setText(folder);
            folderText.setVisibility(View.VISIBLE);
            linkButton.setVisibility(View.VISIBLE);
            gotoPackCreationMode(false);
        }

        if (publishNoteContainer != null)
            if (publishNoteIsHidden) publishNoteContainer.setVisibility(View.GONE);
            else publishNoteContainer.setVisibility(View.VISIBLE);

        isInPackCreationMode = false;

        if (recyclerView != null) {
            adapter = new PackAdapter(
                    (BaseActivity) getActivity(),
                    this,
                    folder,
                    BaseActivity.USER_STICKERS_DIRECTORY,
                    BaseActivity.Companion.getBASE_USER_THUMBNAIL_DIRECTORY());

            if (BaseActivity.Companion.isTablet() || BaseActivity.Companion.isInLandscape())
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
            else
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

            recyclerView.setAdapter(adapter);
            setRetainInstance(false);
            new AsyncTaskPackAdapter().execute();
        }
    }


    private class AsyncTaskPackAdapter extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... folders) {
//            Log.e("UserIconPackDetailed", folder);
            adapter.refresh();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            if (recyclerView != null)
                recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyItemRange();
        }
    }

    private class AsyncTaskAddWhatsapp extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... folders) {
//            Log.e("UserIconPackDetailed", folder);
            updateWhatsappExportFiles(folder);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            addStickerPackToWhatsApp(folder, folder);
        }
    }

    private static final int ADD_PACK = 200;
    private static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id";
    private static final String EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority";
    private static final String EXTRA_STICKER_PACK_NAME = "sticker_pack_name";

    private void addStickerPackToWhatsApp(String identifier, String stickerPackName) {
        FragmentActivity activity = getActivity();
        Context context = getContext();
        if (activity != null && context != null) {
            try {
                //if neither WhatsApp Consumer or WhatsApp Business is installed, then tell user to install the apps.
                if (WhitelistCheck.isWhatsAppConsumerAppNotInstalled(activity.getPackageManager()) && WhitelistCheck.isWhatsAppSmbAppNotInstalled(activity.getPackageManager())) {
                    Toast.makeText(context, R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
                    return;
                }
                final boolean stickerPackWhitelistedInWhatsAppConsumer = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(context, identifier);
                final boolean stickerPackWhitelistedInWhatsAppSmb = WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(context, identifier);
                if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
                    //ask users which app to add the pack to.
                    launchIntentToAddPackToChooser(identifier, stickerPackName);
                } else if (!stickerPackWhitelistedInWhatsAppConsumer) {
                    launchIntentToAddPackToSpecificPackage(identifier, stickerPackName, WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME);
                } else if (!stickerPackWhitelistedInWhatsAppSmb) {
                    launchIntentToAddPackToSpecificPackage(identifier, stickerPackName, WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME);
                } else {
                    Toast.makeText(context, R.string.is_sticker_already, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(context, R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
            }
        }

    }

    private void launchIntentToAddPackToSpecificPackage(String identifier, String stickerPackName, String whatsappPackageName) {
        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        intent.setPackage(whatsappPackageName);
        try {
            startActivityForResult(intent, ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
        }
    }

    //Handle cases either of WhatsApp are set as default app to handle this intent. We still want users to see both options.
    private void launchIntentToAddPackToChooser(String identifier, String stickerPackName) {
        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.add_to_whatsapp)), ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private Intent createIntentToAddStickerPack(String identifier, String stickerPackName) {
        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        intent.putExtra(EXTRA_STICKER_PACK_ID, identifier);
        intent.putExtra(EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY);
        intent.putExtra(EXTRA_STICKER_PACK_NAME, stickerPackName);
        return intent;
    }

    private static void updateWhatsappExportFiles(@NotNull String userPackName) {
        Intrinsics.checkParameterIsNotNull(userPackName, "userPackName");
        File folder = new File(BaseActivity.USER_STICKERS_DIRECTORY + userPackName + File.separator);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            int i = 0;
            if (files == null) {
                Intrinsics.throwNpe();
            }

            for (int var5 = files.length; i < var5; ++i) {
                String pngDirectory = folder.getPath() + File.separator + i + ".png";
                String webFolderDirectory = BaseActivity.BASE_PHONE_WHATSAPP_WEBP_DIRECTORY + userPackName + File.separator;
                File webFolder = new File(webFolderDirectory);

                if (!webFolder.exists()) {
                    webFolder.mkdirs();
                }

                String webpDirectory = webFolderDirectory + i + ".webp";
                File webpFile = new File(webpDirectory);
                if (webpFile.exists())
                    webpFile.delete();

                try {

                    Bitmap webpBitmap = BitmapFactory.decodeFile(pngDirectory);
                    if (i == 0) {
                        Bitmap.createScaledBitmap(
                                webpBitmap, 96, 96, false).compress(
                                Bitmap.CompressFormat.PNG, 50, new FileOutputStream(
                                        webFolderDirectory + "trayImageFile" + ".png"
                                )
                        );
                    }

                    Bitmap tempBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(tempBitmap);
                    Intrinsics.checkExpressionValueIsNotNull(webpBitmap, "webpBitmap");
                    canvas.drawBitmap(webpBitmap, (float) (512 - webpBitmap.getWidth()) / 2.0F, (float) (512 - webpBitmap.getHeight()) / 2.0F, null);
                    tempBitmap.compress(Bitmap.CompressFormat.WEBP, 80, new FileOutputStream(webpDirectory));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
