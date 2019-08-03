package com.amir.stickergram.sticker.pack.user;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import com.amir.stickergram.HelpActivity;
import com.amir.stickergram.R;
import com.amir.stickergram.UserStickersActivity;
import com.amir.stickergram.base.BaseActivity;
import com.amir.stickergram.base.BaseFragment;
import com.amir.stickergram.infrastructure.Loader;

import java.io.File;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        isInPackCreationMode = false;
        View view = inflater.inflate(R.layout.fragment_user_detailed_pack, container, false);
        setFont((ViewGroup) view);
        recyclerView = view.findViewById(R.id.fragment_user_detailed_pack_list);
        folderText = view.findViewById(R.id.fragment_user_detailed_pack_text_folder);
        linkButton = view.findViewById(R.id.fragment_user_detailed_pack_pack_creation_mode);

        View publishNoteCloseButton = view.findViewById(R.id.include_detailed_note_close);
        publishNoteText = view.findViewById(R.id.include_detailed_note_text);
        View publishIcon = view.findViewById(R.id.include_detailed_note_info_icon);
        publishNoteContainer = view.findViewById(R.id.include_detailed_note_container);

        progressBar = view.findViewById(R.id.fragment_icon_detailed_progressBar);

        if (publishNoteCloseButton != null &&
                publishIcon != null &&
                publishNoteText != null &&
                linkButton != null &&
                publishNoteContainer != null) {
            publishNoteText.setOnClickListener(this);
            publishNoteText.setOnClickListener(this);
            publishNoteCloseButton.setOnClickListener(this);
            linkButton.setOnClickListener(this);
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
        if (itemId == R.id.fragment_user_detailed_pack_pack_creation_mode) {
            if (isInPackCreationMode) {
                Loader.goToBotInTelegram((BaseActivity) getActivity());
            } else {
                gotoPackCreationMode(true);
            }
        } else if (itemId == R.id.include_detailed_note_close) {
            if (publishNoteContainer != null) {
                publishNoteContainer.setVisibility(View.GONE);
                publishNoteIsHidden = true;
//                Log.e(getClass().getSimpleName(), "on click: " + publishNoteIsHidden);
            }
        } else if (itemId == R.id.include_detailed_note_text || itemId == R.id.include_detailed_note_info_icon) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), HelpActivity.class));
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
            linkButton.setVisibility(View.GONE);
            isInPackCreationMode = false;
            publishNoteText.setText(getString(R.string.to_save_permanently));
//            publishNoteContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnIconClicked(final PackItem item) {
        final BaseActivity activity = (BaseActivity) getActivity();
        if (isInPackCreationMode)
            sendImageToBot(activity, item);
        else
            sendImageToUser(activity, item);

    }

    private void sendImageToUser(final BaseActivity activity, final PackItem item) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    if (Loader.getActivePack() != null) {
//                    if (BaseActivity.isTelegramInstalled) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setPackage(Loader.getActivePack());
                        intent.setType("application/pdf");
//                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(new File(item.getWebpDir()).toString()));
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(item.getWebpDir())));
//                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
//                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivity(intent);
                    } else
                        Toast.makeText(activity, getString(R.string.telegram_is_not_installed_you_can_t_create_sticker), Toast.LENGTH_LONG).show();
                } else if (which == Dialog.BUTTON_NEUTRAL) {
                    sendImageToBot((BaseActivity) UserIconPackDetailedFragment.this.getActivity(), item);
                    Toast.makeText(getContext(), getString(R.string.choose_the_stickers_bot), Toast.LENGTH_LONG).show();
                }
            }
        };

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_single_item, null, false);
        setFont((ViewGroup) view);
        TextView textView = (TextView) view.findViewById(R.id.dialog_single_item_title);
        textView.setText(activity.getString(R.string.do_you_want_to_send_this_sticker));
        ImageView stickerImage = (ImageView) view.findViewById(R.id.dialog_single_item_image);
        if (stickerImage != null)
            stickerImage.setImageBitmap(BitmapFactory.decodeFile(item.getDir()));

        final AlertDialog sendImageToUserDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
//                .setTitle(activity.getString(R.string.do_you_want_to_send_this_sticker))
                .setNegativeButton(activity.getString(R.string.no), listener)
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



    private void sendImageToBot(final BaseActivity activity, final PackItem item) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    if (Loader.getActivePack() != null) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setPackage(Loader.getActivePack());
                        intent.setType("text/plain");
//                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(new File(item.getDir()).toString()));
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(item.getDir())));
                        startActivity(intent);
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
        ImageView stickerImage = (ImageView) view.findViewById(R.id.dialog_send_sticker_image_view);

        if (BaseActivity.isInLandscape) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.RIGHT_OF, R.id.dialog_send_sticker_note_container);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(RelativeLayout.END_OF, R.id.dialog_send_sticker_note_container);
            }
            stickerImage.setLayoutParams(params);
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams((int) (200 * BaseActivity.density), ViewGroup.LayoutParams.WRAP_CONTENT);
            View noteText = view.findViewById(R.id.dialog_send_sticker_note_container);
            if (noteText != null) noteText.setLayoutParams(params2);
        }

        Bitmap bitmap = BitmapFactory.decodeFile(item.getDir());
        if (stickerImage != null)
            stickerImage.setImageBitmap(bitmap);

        final AlertDialog sendToBotDialog = new AlertDialog.Builder(getActivity())
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

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_single_item, null, false);
        setFont((ViewGroup) view);
        TextView textView = (TextView) view.findViewById(R.id.dialog_single_item_title);
        textView.setText(activity.getString(R.string.do_you_want_to_delete_this_sticker));

        ImageView stickerImage = (ImageView) view.findViewById(R.id.dialog_single_item_image);

        Bitmap bitmap = BitmapFactory.decodeFile(item.getDir());
        if (stickerImage != null)
            stickerImage.setImageBitmap(bitmap);

        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity())
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

    @Override
    public void folderDeleted() {
        //restarting the activity without animation
        //that's the way to do it
        getActivity().finish();
        startActivity(new Intent(getActivity(), UserStickersActivity.class));
        getActivity().overridePendingTransition(0, 0);
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
                    BaseActivity.BASE_USER_THUMBNAIL_DIRECTORY);

            if (BaseActivity.isTablet || BaseActivity.isInLandscape)
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
}
