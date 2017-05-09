
package com.mxn.soul.flowingdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by mxn on 2016/12/13.
 * MenuListFragment
 */

public class MenuListFragment extends Fragment {

    private ImageView ivMenuUserProfilePhoto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container,
                false);
        ivMenuUserProfilePhoto = (ImageView) view.findViewById(R.id.ivMenuUserProfilePhoto);
        NavigationView vNavigation = (NavigationView) view.findViewById(R.id.vNavigation);
        vNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_teac:  //教师
                        Intent intent = new Intent(MenuListFragment.this.getActivity(), TeacherListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.menu_cour:  //课程
                        Intent intent2 =new Intent(MenuListFragment.this.getActivity(),CourseListActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.menu_grad:  //班级
                        Intent intent3 =new Intent(MenuListFragment.this.getActivity(),GradesListActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.menu_room:  //教室
                        Intent intent4 =new Intent(MenuListFragment.this.getActivity(),RoomListActivity.class);
                        startActivity(intent4);
                        break;
                    default:
                        Toast.makeText(MenuListFragment.this.getActivity(), "暂没开通，敬请期待", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        setupHeader();
        return view;
    }

    private void setupHeader() {
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.global_menu_avatar_size);
        String profilePhoto = getResources().getString(R.string.user_profile_photo);
        Picasso.with(getActivity())
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivMenuUserProfilePhoto);
    }

}
