package org.tensorflow.lite.examples.detection

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.opengl.Visibility
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_elements_found.*

class ElementsFoundActivity : AppCompatActivity() {

    private lateinit var symbol: TextView
    private lateinit var number: TextView
    private lateinit var name: TextView
    private lateinit var weight: TextView
    private lateinit var titleElement: TextView
    private lateinit var shittyLayout: ConstraintLayout
    private lateinit var sharedPref: SharedPreferences
    private lateinit var collectedElements: MutableSet<String>
    private lateinit var pointsTextView: TextView
    private lateinit var celebrate: Runnable
    private lateinit var mp: MediaPlayer
    private lateinit var vibrator: Vibrator
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elements_found)
        handler = Handler()
        mp = MediaPlayer.create(this,R.raw.celebrate)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        sharedPref = getSharedPreferences("test",Context.MODE_PRIVATE)
        collectedElements = sharedPref.getStringSet("elements", mutableSetOf())!!

        val symbolText = intent.getStringExtra("symbol")
        val numberText = intent.getIntExtra("number", -1)
        val nameText = intent.getStringExtra("name")
        val weightText = intent.getStringExtra("weight")
        val objectText = intent.getStringExtra("object_found")

        symbol = findViewById<TextView>(R.id.element_symbol_found)
        number = findViewById<TextView>(R.id.element_number_found)
        name = findViewById<TextView>(R.id.element_name_found)
        weight = findViewById<TextView>(R.id.element_weight_found)
        titleElement = findViewById(R.id.titleElement)
        shittyLayout = findViewById(R.id.shittyLayout)
        pointsTextView = findViewById(R.id.points)

        celebrate = Runnable { //play animation in another activity...
            mp.isLooping = false
            mp.start()
            if (vibrator.hasVibrator()) { // Vibrator availability checking
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE)) // New vibrate method for API Level 26 or higher
                } else {
                    vibrator.vibrate(5000) // Vibrate method for below API Level 26
                }
            }
        }

        if (numberText == -1) {
            titleElement.text = "No element found in ${objectText.capitalize()}"
            shittyLayout.visibility = View.INVISIBLE
        } else {
            titleElement.text = "Element found in ${objectText.capitalize()}!"
            symbol.text = symbolText
            number.text = numberText.toString()
            name.text = nameText
            weight.text = weightText
            if(collectedElements.contains(numberText.toString())){
                var score = sharedPref.getInt("POINTS", 0)
                pointsTextView.text = "No points given. You have already found this element"
            } else {
                pointsTextView.text = "You earned ${number.text} points!"
                var score = sharedPref.getInt("POINTS", 0)
                score += numberText
                sharedPref.edit().putInt("POINTS", score).apply()
                celebrate()
            }
        }

        collectedElements.add(numberText.toString())

        setValues()



    }

    private fun celebrate() {
        handler.post(celebrate)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(celebrate)
        mp.stop()
        vibrator.cancel()
    }



    private fun setValues() {
        sharedPref.edit().putStringSet("elements", collectedElements).apply()
    }
}
