package com.mxn.soul.flowingdrawer;

import com.mxn.soul.flowingdrawer.network.Repo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by weijie.liu on 17/04/2017.
 */

public interface NetTest {
    @GET("/users/{user}/repos")
    Call<List<Repo>> getBaidu(@Path("user") String user);
}
