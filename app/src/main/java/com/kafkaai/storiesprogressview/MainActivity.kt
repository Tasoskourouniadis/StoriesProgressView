package com.kafkaai.storiesprogressview


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import com.kafkaai.storiesprogressview.databinding.ActivityMainBinding
import com.kafkaai.storiesprogressview.storiesprogressview.StoriesProgressView


class MainActivity : AppCompatActivity(), StoriesProgressView.StoriesListener{

    private lateinit var binding : ActivityMainBinding

    private var counter = 0
    private val resources = intArrayOf(R.drawable.sample1, R.drawable.sample2, R.drawable.sample3, R.drawable.sample4, R.drawable.sample5, R.drawable.sample6)

    var pressTime = 0L
    var limit = 500L

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                binding.storiesProgressView.pause()
                return@OnTouchListener false
            }

            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                binding.storiesProgressView.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.storiesProgressView.setStoriesCount(resources.size)
        binding.storiesProgressView.setStoryDuration(3000L)

        binding.storiesProgressView.setStoriesListener(this)
//        counter = 2
//        binding.storiesProgressView.startStories(counter)
        binding.storiesProgressView.startStories()

        binding.storyContentImageView.setImageResource(resources[counter])

        binding.reverse.setOnTouchListener(onTouchListener)
        binding.reverse.setOnClickListener {
            binding.storiesProgressView.reverse()
        }
        binding.skip.setOnTouchListener(onTouchListener)
        binding.skip.setOnClickListener {
            binding.storiesProgressView.skip()
        }
    }

    override fun onNext() {
        if (counter < resources.size) {
            counter++
            binding.storyContentImageView.setImageResource(resources[counter])
        }
    }

    override fun onPrev() {
        if (counter > 0) {
            counter--
            binding.storyContentImageView.setImageResource(resources[counter])
        }

    }

    override fun onComplete() {
    }

    override fun onDestroy() {
        binding.storiesProgressView.destroy()
        super.onDestroy()
    }
}