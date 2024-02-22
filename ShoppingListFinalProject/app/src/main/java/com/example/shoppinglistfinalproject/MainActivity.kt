package com.example.shoppinglistfinalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.shoppinglistfinalproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Using binding for interacting with views of the app
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(ShoppingList())

        //selecting a view based on the id of menu item
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.shoppinglist -> replaceFragment(ShoppingList())
                R.id.todolist -> replaceFragment(ToDoList())
                R.id.info-> replaceFragment(Info())

                else -> {

                }
            }
            true
        }
    }
    //Function for changing fragments in the main
    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }


}