package com.example.coffeeshop.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.Adapter.CartAdapter
import com.example.coffeeshop.R
import com.example.coffeeshop.databinding.ActivityCartBinding
import com.example.project1762.Helper.ManagmentCart
import com.uilover.project195.Helper.ChangeNumberItemsListener
import kotlin.math.round

class CartActivity : AppCompatActivity() {

    lateinit var binding: ActivityCartBinding
    lateinit var managmentCart: ManagmentCart
    private var tax:Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart= ManagmentCart(this)

        calculateCart()
        setVariale()
        initCartList()
    }

    private fun initCartList() {
        binding.apply {
            cartView.layoutManager=
                LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
            cartView.adapter=CartAdapter(
                managmentCart.getListCart(),
                this@CartActivity,
                object : ChangeNumberItemsListener{
                    override fun onChanged() {
                        calculateCart()
                    }

                }
            )
        }
    }

    private fun setVariale() {
        binding.backBtn.setOnClickListener { finish() }
        binding.checkoutBtn.setOnClickListener {
            managmentCart.clearAllItems(object : ChangeNumberItemsListener {
                override fun onChanged() {
                    // Reinitialize the adapter with the cleared cart
                    binding.cartView.adapter = CartAdapter(
                        managmentCart.getListCart(),
                        this@CartActivity,
                        object : ChangeNumberItemsListener {
                            override fun onChanged() {
                                calculateCart()
                            }
                        }
                    )
                    calculateCart()
                }
            })
            Toast.makeText(this, "The order has been paid", Toast.LENGTH_SHORT).show()
        }
    }



    private fun calculateCart() {
        val percentTax=0.02
        val delivery=15
        tax=Math.round((managmentCart.getTotalFee()*percentTax)*100)/100.0
        val total=Math.round((managmentCart.getTotalFee()+tax+delivery)*100)/100
        val itemTotal=Math.round(managmentCart.getTotalFee()*100)/100
        binding.apply {
            totalFeeTxt.text="$$itemTotal"
            taxTxt.text="$$tax"
            deliveryTxt.text="$$delivery"
            totalTxt.text="$$total"
        }
    }
}