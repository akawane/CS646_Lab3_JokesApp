package com.akawane0813.jokesviewer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.math.abs

// Define a constant value for the key of the joke state in the Bundle
const val JOKE_STATE = "jokeState"

// Define the main Fragment class
class JokesFragment : Fragment() {

    // Define two variables for storing touch event and joke index
    private var initTouchY = 0
    private var jokeIndex = 0

    // Define a variable for storing the reference to the TextView displaying the joke
    private lateinit var jokeTextView: TextView

    // Define a lazy initialization of the jokes array using the resources
    private val jokesArray by lazy { resources.getStringArray(R.array.jokes) }

    // Define a companion object with a factory method for creating the Fragment instance
    companion object {
        fun newInstance() = JokesFragment()
    }

    // Override the onCreateContextMenu() method to inflate the context menu
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.context_menu, menu)
    }

    // Override the onCreateView() method to inflate the Fragment view
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the parent view using the specified layout resource
        val parentView = inflater.inflate(R.layout.fragment_joke, container, false)
        // Retrieve the reference to the joke TextView and store it in a variable
        jokeTextView = parentView.findViewById(R.id.jokeTextView)
        // Register the TextView for context menu handling
        registerForContextMenu(jokeTextView)

        // Add a touch listener to the parent view to allow swiping the joke text up and down
        parentView.setOnTouchListener { v, event ->
            var returnVal = true
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initTouchY = event.y.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val y = event.y.toInt()
                    // If the vertical displacement exceeds 300 pixels, update the joke index and display the new joke
                    if (abs(y - initTouchY) >= 300) {
                        jokeIndex += if (y > initTouchY) -1 else 1
                        updateJoke()
                        initTouchY = y
                    }
                }
                else -> returnVal = false
            }
            returnVal
        }

        return parentView
    }

    // Override the onViewCreated() method to restore the state of the Fragment and display the current joke
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // If there is a saved instance state, restore the joke index from it
        if (savedInstanceState != null) {
            jokeIndex = savedInstanceState.getInt(JOKE_STATE)
        }
        // Display the current joke
        updateJoke()
    }

    // Override the onSaveInstanceState() method to save the current joke index in the Bundle
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(JOKE_STATE, jokeIndex)
    }

    // Override the onContextItemSelected() method to handle the context menu selection
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.next -> {
                jokeIndex++
                updateJoke()
                true
            }
            R.id.prev -> {
                jokeIndex--
                updateJoke()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    /**
    This method updates the joke displayed in the TextView.
    If the joke index is out of bounds of the jokesArray, an AlertDialog is shown indicating that
    there are no more jokes.
    Otherwise, the jokeTextView's text is set to the current joke in jokesArray.
     */
    private fun updateJoke() {
        if (jokeIndex < 0 || jokeIndex >= jokesArray.size) {
            AlertDialog.Builder(requireContext())
                .setTitle("No jokes before this one")
                .setMessage("Click Ok to dismiss.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return
        }
        jokeTextView.text = jokesArray[jokeIndex]
    }
}
