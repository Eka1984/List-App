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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import android.content.Context
import android.widget.Toast


class ToDoList : Fragment() {
    private lateinit var itemsContainer : LinearLayout
    private lateinit var addItemButton : Button
    private lateinit var inputField : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_do_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addItemButton = view.findViewById(R.id.addItemButton)
        inputField = view.findViewById(R.id.editTextInsertItem)
        itemsContainer = view.findViewById(R.id.itemsContainer)

        //Listener for Add button
        addItemButton.setOnClickListener {
            val itemText = inputField.text.toString()
            if (itemText.isNotEmpty()) {
                addItem(itemText)
                inputField.text.clear()
            }else{
                Toast.makeText(requireContext(), "Please enter a note before adding.", Toast.LENGTH_SHORT).show()
            }
        }

        //Displaying notes on the loading of the page
        displayNotes()

    }

    //Function for displaying the notes in the page
    private fun displayNotes() {

        itemsContainer.removeAllViews()

        val notes = loadItems()

        notes.forEachIndexed { index, note ->
            //creating layout
            val noteLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    // Converting 16dp to pixels and set as bottom margin
                    it.bottomMargin = 16.dpToPx(requireContext())
                }
                this.layoutParams = layoutParams
                //setting alterating background color for every second note
                background = if (index % 2 == 0) ContextCompat.getDrawable(requireContext(), R.drawable.rounded_note_background_color1)
                else ContextCompat.getDrawable(requireContext(), R.drawable.rounded_note_background_color2)
                setPadding(16, 16, 16, 16)
            }

            //creating TextView
            val textView = TextView(requireContext()).apply {
                text = note
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            //creating ImageButton
            val deleteButton = ImageButton(requireContext()).apply {
                setImageResource(android.R.drawable.ic_delete)
                setColorFilter(Color.BLACK)
                setBackgroundColor(Color.TRANSPARENT) // or use a suitable background
                setOnClickListener {
                    //checking with user if they really want to delete the note
                    showDeleteConfirmationDialog(note) // Implement deletion logic
                }
            }

            //Adding text and button to the layout and the layout to the container for it
            noteLayout.addView(textView)
            noteLayout.addView(deleteButton)
            itemsContainer.addView(noteLayout)
        }
    }

    //Function for adding notes to the list
    private fun addItem(item: String) {
        val items = loadItems().toMutableList()
        items.add(item)
        saveItems(items)
        displayNotes()
    }

    //Function for deleting notes based on their content
    private fun deleteItem(itemContent: String) {
        val items = loadItems().toMutableList()
        items.remove(itemContent) // Remove by content
        saveItems(items)
        displayNotes()
    }

    //Function for loading items from the file in list format
    private fun loadItems(): List<String> {
        val file = File(requireActivity().getExternalFilesDir(null), "todolist.txt")
        if (!file.exists()) {
            return emptyList()
        }
        return file.readLines()
    }

    //Function for saving items to the file
    private fun saveItems(items: List<String>) {
        val file = File(requireActivity().getExternalFilesDir(null), "todolist.txt")
        file.writeText(items.joinToString("\n"))
    }

    // Utility function to convert dp to pixel for consistency across different screen densities
    fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    //Function for checking with user if they want to delete the note
    private fun showDeleteConfirmationDialog(itemContent: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Confirm Deletion")
            setMessage("Are you sure you want to delete the note?")
            setPositiveButton("Delete") { dialog, which ->
                // User clicked "Delete" button, delete the items
                deleteItem(itemContent)
                Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Cancel", null)
        }.show()
    }


}