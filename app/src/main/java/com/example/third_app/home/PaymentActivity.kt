package com.example.third_app.home

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.third_app.R
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PaymentActivity : AppCompatActivity() {
    var userId: String? =null
    var itemId: String? =null
    var joinedIt: Boolean? =null
    var itemPrice: Int? =null
    var budget: Int? =null
    var new_budget: Int? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val paymenting:TextView=findViewById(R.id.paymenting)
        val paymentCount:TextView=findViewById(R.id.paymentCount)
        val payment:TextView=findViewById(R.id.payment)
        val currentBudget:TextView=findViewById(R.id.currentBudget)
        val afterPayment:TextView=findViewById(R.id.afterPayment)
        val afterBudget:TextView=findViewById(R.id.afterBudget)
        val paying:TextView=findViewById(R.id.payBtn)

        if(intent.hasExtra("userId")){
            userId= intent.getStringExtra("userId").toString()
        }
        if(intent.hasExtra("itemId")){
            itemId= intent.getStringExtra("itemId").toString()
        }
        if(intent.hasExtra("itemPrice")){
            itemPrice= intent.getStringExtra("itemPrice")?.toInt()
        }
        joinedIt= intent.getStringExtra("joinedIt").toBoolean()
        budget= intent.getStringExtra("budget")?.toInt()
        new_budget= intent.getStringExtra("new_budget")?.toInt()

        Log.e("Payment!!!!!userId",userId.toString())
        Log.e("Payment!!!!!itemId",itemId.toString())
        Log.e("Payment!!!!!itemPrice",itemPrice.toString())
        Log.e("Payment!!!!!joinedIt",joinedIt.toString())
        Log.e("Payment!!!!!budget",budget.toString())
        Log.e("Payment!!!!!new_budget",new_budget.toString())

        if(joinedIt as Boolean){//true==?????? ??????
            //findViewById ??????
            paymenting.text="?????? ?????????"
            paymentCount.text="?????? ??????"
            payment.text=itemPrice.toString()
            currentBudget.text=budget.toString()
            afterPayment.text="?????? ??? ?????? : "
            afterBudget.text=new_budget.toString()
            paying.text="????????????"
        }
        else{//?????? ??????
            //findViewById ????????????
            paymenting.text="?????? ?????????"
            paymentCount.text="?????? ??????"
            payment.text=itemPrice.toString()
            currentBudget.text=budget.toString()
            afterPayment.text="?????? ??? ?????? : "
            afterBudget.text=new_budget.toString()
            paying.text="????????????"
        }

        val btn:android.widget.Button=findViewById(R.id.payBtn)
        btn.setOnClickListener {
            //????????? ??????_retrofit
            var joinedItemFullImage:JoinedItemFullImage?=null

            //???????????? ?????? ??????
            var retrofit= Retrofit.Builder()
                .baseUrl("http://192.249.18.195:80")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            //LikedFoodFullImageService ????????? ?????????
            var joinedItemFullImageService: JoinedItemFullImageService =retrofit.create(JoinedItemFullImageService::class.java)

            var dialog= AlertDialog.Builder(this) //FoodFullImageActivity??? ?????? ?????? ?????? ??????..?
//            dialog.setTitle("?????? ????????? ?????????????????????????")
//            dialog.show()

            joinedItemFullImageService.requestJoined(itemId, userId!!).enqueue(object :
                Callback<JoinedItemFullImage> {
                override fun onFailure(call: Call<JoinedItemFullImage>, t: Throwable) {
                    Log.e("FoodFullImageActivity","error : ?????? ??????/?????? PUT ??????")
                    dialog.setMessage("?????? ??????/????????? ??????????????????.")
//                    Toast.makeText(applicationContext,"error : ???????????? ??????", Toast.LENGTH_SHORT)
                }

                override fun onResponse(
                    call: Call<JoinedItemFullImage>,
                    response: Response<JoinedItemFullImage>
                ) {
                    joinedItemFullImage=response.body()
                    Log.e("Joined", response.body().toString())
                    if(joinedItemFullImage == null){
                        Log.e("Joined", "res ??????")
                    }

                    Log.d("Joined:", "statusCode : "+joinedItemFullImage?.statusCode.toString())
                    Log.d("Joined:", "Msg : "+joinedItemFullImage?.statusMsg.toString())

                    //Toast.makeText(applicationContext, login?.statusMsg.toString(), Toast.LENGTH_SHORT)
                    if(joinedItemFullImage?.statusCode.toString()=="201"){ //toString ????????
                        var joinedProduct = joinedItemFullImage!!.data //nowJoined ?????? ?????????


                        if(joinedIt as Boolean) Toast.makeText(this@PaymentActivity,"?????? ?????? ?????? ??????!", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(this@PaymentActivity,"?????? ?????? ?????? ??????!", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        //Toast.makeText(this, "error : "+likedItemFullImage?.statusCode.toString(), Toast.LENGTH_SHORT)
                        Log.e("Joined","error: "+joinedItemFullImage?.statusCode.toString())
                        dialog.setMessage(joinedItemFullImage?.statusMsg.toString())
                        dialog.show()
                    }
                }
            })

            val intent= Intent(this@PaymentActivity, FoodFullImageActivity::class.java)
            startActivity(intent)
        }

        val backBtn:android.widget.Button=findViewById(R.id.goBack)
        backBtn.setOnClickListener {
            val intent= Intent(this@PaymentActivity, FoodFullImageActivity::class.java)
            startActivity(intent)
        }







    }
}