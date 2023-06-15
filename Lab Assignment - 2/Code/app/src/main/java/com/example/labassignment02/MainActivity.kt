package com.example.labassignment02

import  androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), Communicator {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentOne = FragmentOne();
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragmentOne).commit(-
    }

    override fun passDataCom(editTextInput: String) {
        val bundle = Bundle()
        bundle.putString("message", editTextInput)

        val transaction = this.supportFragmentManager.beginTransaction()
        val fragmentTwo = FragmentTwo()

        fragmentTwo.arguments = bundle

        transaction.replace(R.id.fragmentContainerView, fragmentTwo)
        transaction.commit()
    }
}