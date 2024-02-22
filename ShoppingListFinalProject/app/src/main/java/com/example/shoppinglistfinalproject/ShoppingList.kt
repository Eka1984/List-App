package com.example.shoppinglistfinalproject

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import java.io.File
import android.widget.TextView
import android.widget.ImageButton
import android.view.ViewGroup.LayoutParams
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AlertDialog




class ShoppingList : Fragment() {
    private lateinit var itemsContainer : LinearLayout
    private lateinit var cleanListButton : Button
    private lateinit var addItemButton : Button
    private lateinit var inputField : EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cleanListButton = view.findViewById(R.id.cleanListButton)
        addItemButton = view.findViewById(R.id.addItemButton)
        inputField = view.findViewById(R.id.editTextInsertItem)
        itemsContainer = view.findViewById(R.id.itemsContainer)

        updateCleanListButtonVisibility()

        addItemButton.setOnClickListener {
            //getting input from inputfield
            val itemText = inputField.text.toString()
            if (itemText.isNotEmpty()) {
                addItem(itemText)
                inputField.text.clear()
            }else{
                //if input field is empty, informing the user about it
                Toast.makeText(requireContext(), "Please enter a note before adding.", Toast.LENGTH_SHORT).show()
            }
        }

        cleanListButton.setOnClickListener {
            //confirming with the user if they really want to clear the list
            showDeleteConfirmationDialog()
        }

        //printing out the list on load of the app
        displayItemsWithDeleteIcons(loadItems())

    }


    private fun addItem(item: String) {
        //getting list of items from the file (mutable - can be edited)
        val items = loadItems().toMutableList()
        //adding new item to the list
        items.add(item)
        //saving to the file
        saveItems(items)
        //printing out the list on the page
        displayItemsWithDeleteIcons(items)
        //checking if the CleanListButton should be shown or not
        updateCleanListButtonVisibility()
    }

    //function for deleting an item from the list
    private fun deleteItem(index: Int) {
        //loading items from the file
        val items = loadItems().toMutableList()
        //checking if the time with given index exists in the list
        if (index in items.indices) {
            //deleting given item
            items.removeAt(index)
            //saving items to the file
            saveItems(items)
        }
        //printing the list on the page
        displayItemsWithDeleteIcons(items)
    }
    //function for cleaning the list
    private fun cleanList() {
        //saving the list to the file as an empty list(clearing it)
        saveItems(emptyList())
        //printing out empty list
        displayItemsWithDeleteIcons(emptyList())
        //updating(hiding cleanlist button)
        updateCleanListButtonVisibility()
    }
    //Function that retrieves items from the list in the file
    private fun loadItems(): List<String> {
        val file = File(requireActivity().getExternalFilesDir(null), "shopping.txt")
        //returning empty list, if no items have been added yet, and file not created
        if (!file.exists()) {
            return emptyList()
        }
        return file.readLines()
    }
    //Function that saves the list to the file
    private fun saveItems(items: List<String>) {
        //creating file object
        val file = File(requireActivity().getExternalFilesDir(null), "shopping.txt")
        //joining the list to a string and overwriting the file with it
        file.writeText(items.joinToString("\n"))
    }

    //function for printing out list items with delete icon
    private fun displayItemsWithDeleteIcons(items: List<String>) {
        // Clear existing views
        itemsContainer.removeAllViews()

        //Iterating through the items
        items.forEachIndexed { index, item ->
            //a new linear layout is created for every item //apply configures properties of the layout
            val itemLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                //setting parameters of the layout
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            //Initializing a new TextView object
            val textView = TextView(requireContext()).apply {
                //setting text to order number of the item and its value
                text = "${index + 1}. $item"
                //setting font size
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    //setting the weight of the element
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            //Initializing a new ImageButton object
            val deleteButton = ImageButton(requireContext()).apply {
                //setting delete icon from drawable
                setImageResource(android.R.drawable.ic_delete)
                //setting the color to black
                setColorFilter(Color.BLACK)
                //setting layout parameters
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                //setting onlclick listener
                setOnClickListener {
                    //deleting the item from the list
                    deleteItem(index) // Delete this item
                }
            }
            //Adding TextView and ImageButton to the current layout
            itemLayout.addView(textView)
            itemLayout.addView(deleteButton)

            //Adding layout to the container for it
            itemsContainer.addView(itemLayout)
        }
    }
    //Function for confirming deletion with the user
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Confirm Deletion")
            setMessage("Are you sure you want to delete all items from the list?")
            setPositiveButton("Delete") { dialog, which ->
                // User clicked "Delete" button, delete the items
                cleanList()
                Toast.makeText(requireContext(), "The list is cleared", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Cancel", null)
        }.show()
    }

    //Function for showing the CleanList button only when the list is not empty
    private fun updateCleanListButtonVisibility() {
        val items = loadItems()
        cleanListButton.visibility = if (items.isNotEmpty()) View.VISIBLE else View.GONE
    }

}
