package com.example.third_app.user

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.third_app.*
import com.example.third_app.category.CategoryApplication
import com.example.third_app.databinding.FragmentUserBinding
import com.example.third_app.home.Product
import com.example.third_app.home.UserApplication
import com.example.third_app.login.Login
import com.example.third_app.login.LoginActivity
import com.example.third_app.login.LogoutService
import com.kakao.sdk.talk.TalkApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserFragment : Fragment() {
    private var mBinding : FragmentUserBinding ?= null
    private val binding get() = mBinding!!
    private lateinit var adapter : PreviousItemListAdapter
    var logout: Login?=null
    var previousitemList: PreviousItemList?=null

    // product List
    var mDatas=mutableListOf<Product>()
    var alertDialog : androidx.appcompat.app.AlertDialog?= null

    // Context를 액티비티로 형변환해서 할당
    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    // sharedManager 선언
    private val sharedManager : SharedManager by lazy { SharedManager(mainActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding= FragmentUserBinding.inflate(inflater, container, false)

        var userName = binding.tvUserName
        var profileImg = binding.ivUser

        // 카카오톡 프로필 가져오기
        TalkApiClient.instance.profile { profile, error ->
            if (error != null) {
                val currentUser = sharedManager.getCurrentUser()
                if(currentUser != null){
                   userName.text = currentUser.name
                }
                else{
                    Log.e(TAG, "카카오톡 프로필 가져오기 실패", error)
                }
            } else if (profile != null) {
                Log.i(
                    TAG, "카카오톡 프로필 가져오기 성공" +
                            "\n닉네임: ${profile.nickname}" +
                            "\n프로필사진: ${profile.thumbnailUrl}"
                )
                userName.text = profile.nickname
                Glide.with(this).load(profile.thumbnailUrl).circleCrop().into(profileImg)
            }
        }

        val intent = Intent(mainActivity, LoginActivity::class.java)

        //retrofit 사용해서 서버에도 업데이트 시켜주기
        // 레트로핏 객체 생성.
        var retrofit=Retrofit.Builder()
            .baseUrl("http://192.249.18.195:80")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //로그아웃 서비스 올리기
        var logoutService: LogoutService = retrofit.create(LogoutService::class.java)

        val currentUser=sharedManager.getCurrentUser()
        var logout_id=currentUser.id

        //logout 버튼 클릭 시 logout 시키기_로컬에서 sharedManager null로 바꿔주기
        binding.logoutBtn.setOnClickListener {
            var dialog=AlertDialog.Builder(mainActivity)
            dialog.setTitle("로그아웃을 하시겠습니까?")

            logoutService.requestLogout(logout_id).enqueue(object :Callback<Login>{
                override fun onFailure(call: Call<Login>, t: Throwable) {
                    Log.e("Logout","error : 로그아웃 호출 실패")
                    dialog.setMessage("로그아웃을 실패했습니다.")
//                    Toast.makeText(applicationContext,"error : 로그아웃 실패", Toast.LENGTH_SHORT)
                }

                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    logout=response.body()
                    Log.e("Logout", response.body().toString())
                    if(logout == null){
                        Log.e("Logout", "res 없음")
                    }

                    Log.d("Logout:", "statusCode : "+logout?.statusCode.toString())
                    Log.d("Logout:", "Msg : "+logout?.statusMsg.toString())

                    //Toast.makeText(applicationContext, login?.statusMsg.toString(), Toast.LENGTH_SHORT)
                    if(logout?.statusCode.toString()=="204"){
                        var logoutData = logout!!.data
                        // 현재 유저 정보 저장

                        sharedManager.logoutCurrentUser(currentUser)
                        UserApplication.deleteUserId()
                        CategoryApplication.deleteCategoryId()

                        Log.d(TAG,"UserFragment!!!!!!id"+sharedManager.getCurrentUser().id)
                        Log.d(TAG,"UserFragment!!!!!!name"+sharedManager.getCurrentUser().name)
                        Log.d(TAG,"UserFragment!!!!!!pw"+sharedManager.getCurrentUser().password)
                        Log.d(TAG,"UserFragment!!!!!!isActive"+sharedManager.getCurrentUser().isActive)

                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        startActivity(intent)
                        dialog.setMessage("로그아웃 완료!")
                    }
                    else{
                        Toast.makeText(mainActivity, "error : "+logout?.statusCode.toString(), Toast.LENGTH_SHORT)
                                        dialog.setMessage(logout?.statusMsg.toString())
                                        dialog.show()
                    }
                }
            })
            alertDialog?.dismiss()
        }



        //User's Previous itemListService
        var itemListService: PreviousItemListService = retrofit.create(PreviousItemListService::class.java)
        itemListService.requestItemList(sharedManager.getCurrentUser().id).enqueue(object :Callback<PreviousItemList>{
            override fun onFailure(call: Call<PreviousItemList>, t: Throwable) {
                Log.e("ItemList","error : itemList 호출 실패")
                adapter = PreviousItemListAdapter()
                binding.ItemListRecyclerView.adapter = adapter //리사이클러뷰에 어댑터 연결
                binding.ItemListRecyclerView.layoutManager = LinearLayoutManager(mainActivity)
            }

            override fun onResponse(call: Call<PreviousItemList>, response: Response<PreviousItemList>) {
                previousitemList=response.body()
                Log.d("ItemList", previousitemList.toString())
                Log.d("ItemList", "statusCode : "+previousitemList?.statusCode.toString())
                Log.d("ItemList", "Msg : "+previousitemList?.statusMsg.toString())
                adapter = PreviousItemListAdapter()
                mDatas = previousitemList?.data as MutableList<Product>
                adapter.datalist = mDatas
                binding.ItemListRecyclerView.adapter = adapter //리사이클러뷰에 어댑터 연결
                binding.ItemListRecyclerView.layoutManager = LinearLayoutManager(mainActivity)
                adapter.notifyDataSetChanged()
            }

        })
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}