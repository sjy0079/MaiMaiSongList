package org.bbs.maimaisonglist

import android.animation.*
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import com.bumptech.glide.Glide
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.bbs.maimaisonglist.model.MSLSongData
import org.bbs.maimaisonglist.model.MSLSongSingleData
import org.bbs.maimaisonglist.utils.WindowUtils
import org.bbs.maimaisonglist.utils.setOnClickListenerAvoidDoubleClick
import org.bbs.maimaisonglist.widgets.MSLDifficultyAdapter
import org.bbs.maimaisonglist.widgets.MSLSongListAdapter
import java.io.IOException
import java.io.InputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var disposable: Disposable
    private var songListAdapter: MSLSongListAdapter? = null
    private var data: MSLSongData? = null
    private var searchingAction = Runnable {
        songListAdapter?.search(searchInput.text.toString().trim())
    }
    private var isSearching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupEarth()
        setupCloud()
        setupView()
        loadData()
    }

    private fun setupView() {
        leftButton.setOnClickListenerAvoidDoubleClick {
            if (difficulty.scrollState == SCROLL_STATE_IDLE) {
                difficulty.setCurrentItem(difficulty.currentItem - 1, true)
            }
            100L
        }
        rightButton.setOnClickListenerAvoidDoubleClick {
            if (difficulty.scrollState == SCROLL_STATE_IDLE) {
                difficulty.setCurrentItem(difficulty.currentItem + 1, true)
            }
            100L
        }
        sortButton.setOnClickListenerAvoidDoubleClick {
            val image = songListAdapter?.changeSortBy()
            songList.scrollToPosition(0)
            sortButton.setImageResource(image ?: 0)
            100L
        }
        searchButton.setOnClickListenerAvoidDoubleClick {
            isSearching = !isSearching
            if (isSearching) {
                searchButton.setImageResource(R.drawable.msl_search_button_bg_highlight)
                animateSearchTransIn()
            } else {
                searchButton.setImageResource(R.drawable.msl_search_button_bg_default)
                animateSearchTransOut()
            }
            300L
        }
        rankFilterButton.setOnClickListenerAvoidDoubleClick {
            val image = songListAdapter?.changeFilterRank()
            songList.scrollToPosition(0)
            rankFilterButton.setImageResource(image ?: 0)
            100L
        }
        difficulty.apply {
            isUserInputEnabled = false
            adapter = MSLDifficultyAdapter()
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    (adapter as MSLDifficultyAdapter).currentPosition = position
                    if (songListAdapter != null) {
                        songList.scrollToPosition(0)
                        songListAdapter!!.setData(findPageData())
                    }
                }
            })
        }
        searchInput.apply {
            addTextChangedListener {
                handler.removeCallbacks(searchingAction)
                handler.postDelayed(searchingAction, 500L)
            }
        }
        songList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            songListAdapter = MSLSongListAdapter()
            adapter = songListAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Glide.with(this@MainActivity).resumeRequests()
                    } else {
                        Glide.with(this@MainActivity).pauseRequests()
                    }
                }
            })
        }
    }

    private fun loadData() {
        disposable = Observable.just("database_v1.json")
            .map {
                var json = String()
                try {
                    val input = assets.open(it)
                    json = convertStreamToString(input)
                } catch (ignore: Exception) {
                }
                json
            }
            .subscribeOn(Schedulers.io())
            .map {
                Gson().fromJson(it, MSLSongData::class.java)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                data = it
                songListAdapter?.setData(findPageData())
            }
    }

    private fun findPageData(): List<MSLSongSingleData> {
        if (data == null) {
            return listOf()
        }
        val difficultyAdapter = difficulty.adapter as MSLDifficultyAdapter
        val nowPageLevel = difficultyAdapter.items[difficultyAdapter.currentPosition]
        for (element in data!!.data!!) {
            if (element.level == nowPageLevel.level) {
                return element.data
            }
        }
        return listOf()
    }

    private fun convertStreamToString(input: InputStream): String {
        var s = String()
        try {
            val scanner = Scanner(input, "UTF-8").useDelimiter("\\A")
            if (scanner.hasNext()) {
                s = scanner.next()
            }
            input.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return s
    }

    private fun animateSearchTransIn() {
        val fadeAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener {
                searchInput.alpha = it.animatedValue as Float
            }
        }
        val transAnimator = ValueAnimator.ofFloat(
            -WindowUtils.dip2px(49F), 0F
        ).apply {
            addUpdateListener {
                searchInput.translationY = it.animatedValue as Float
                songList.translationY = it.animatedValue as Float + WindowUtils.dip2px(49F)
            }
            addListener(
                onStart = {
                    searchInput.visibility = View.VISIBLE
                    searchInput.alpha = 0F
                },
                onEnd = {
                    val params = songList.layoutParams as RelativeLayout.LayoutParams
                    params.bottomMargin = WindowUtils.dip2px(49F).toInt()
                    songList.layoutParams = params
                })
        }
        AnimatorSet().apply {
            playTogether(fadeAnimator, transAnimator)
            duration = 300L
        }.start()
    }

    private fun animateSearchTransOut() {
        val fadeAnimator = ValueAnimator.ofFloat(1F, 0F).apply {
            addUpdateListener {
                searchInput.alpha = it.animatedValue as Float
            }
        }
        val transAnimator = ValueAnimator.ofFloat(
            0F, -WindowUtils.dip2px(49F)
        ).apply {
            addUpdateListener {
                searchInput.translationY = it.animatedValue as Float
                songList.translationY = it.animatedValue as Float + WindowUtils.dip2px(49F)
            }
            addListener(
                onStart = {
                    searchInput.alpha = 1F
                },
                onEnd = {
                    val params = songList.layoutParams as RelativeLayout.LayoutParams
                    params.bottomMargin = 0
                    songList.layoutParams = params
                    searchInput.visibility = View.GONE
                })
            duration = 300L
        }
        AnimatorSet().apply {
            playTogether(fadeAnimator, transAnimator)
            duration = 300L
        }.start()
    }

    private fun setupEarth() {
        val animator = ObjectAnimator.ofFloat(earth, "rotation", 0F, 360F).apply {
            duration = 80000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
        earth.post {
            earth.translationY = earth.measuredHeight.toFloat() / 2
            animator.start()
        }
    }

    private fun setupCloud() {
        val cloud1Animator = ObjectAnimator.ofFloat(
            cloud1, "translationX",
            -WindowUtils.dip2px(400F), WindowUtils.getWindowWidth(this)
        ).apply {
            duration = 80000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationRepeat(animation: Animator?) {
                    cloud1.translationY = (Math.random() * 160).toFloat()
                }
            })
        }
        val cloud2Animator = ObjectAnimator.ofFloat(
            cloud2, "translationX",
            -WindowUtils.dip2px(120F), WindowUtils.getWindowWidth(this)
        ).apply {
            duration = 90000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationRepeat(animation: Animator?) {
                    cloud2.translationY = (Math.random() * 160).toFloat()
                }
            })
        }
        val cloud3Animator = ObjectAnimator.ofFloat(
            cloud3, "translationX",
            WindowUtils.getWindowWidth(this), -WindowUtils.dip2px(80F)
        ).apply {
            duration = 60000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationRepeat(animation: Animator?) {
                    cloud3.translationY = (Math.random() * 160).toFloat()
                }
            })
        }
        cloud1.post {
            cloud1.visibility = View.VISIBLE
            cloud1Animator.start()
        }
        cloud2.post {
            cloud2.visibility = View.VISIBLE
            cloud2Animator.start()
        }
        cloud3.post {
            cloud3.visibility = View.VISIBLE
            cloud3Animator.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
