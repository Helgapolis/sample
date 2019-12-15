package com.kastapp.sample.ui.feed

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.kastapp.sample.R
import com.kastapp.sample.data.model.Feed
import com.kastapp.sample.databinding.ActivityFeedItemBinding
import com.kastapp.sample.ui.common.AbsActivity
import com.kastapp.sample.ui.common.ext.createTransition
import com.kastapp.sample.util.doOnEnd

class FeedItemActivity : AbsActivity() {

    companion object {
        const val TRANSITION_IMAGE = "image"
        const val TRANSITION_TITLE = "title"
        const val TRANSITION_CARD = "card"
        const val EXTRA_FEED = "EXTRA_FEED"
    }

    private lateinit var binding: ActivityFeedItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed_item)
        supportPostponeEnterTransition()
        val feed = intent.extras!!.getParcelable<Feed>(EXTRA_FEED)!!

        window.apply {
            binding.title.transitionName = TRANSITION_TITLE + "-${feed.id}"
            binding.image.transitionName = TRANSITION_IMAGE + "-${feed.id}"
            binding.card.transitionName = TRANSITION_CARD + "-${feed.id}"
            sharedElementEnterTransition = createTransition(R.transition.feed_item_shared_enter_3)
            enterTransition = createTransition(R.transition.feed_item_enter_3)
            returnTransition = createTransition(R.transition.feed_item_return_3)

            // простые анимации, чтобы они корректно работали надо убрать transitionName у вьюх
            // enterTransition = createTransition(R.transition.feed_item_enter_1)
            // enterTransition = createTransition(R.transition.feed_item_enter_2)
        }

        supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title = feed.title
        }

        binding.apply {
            title.text = feed.title
            text.text = feed.text
            Glide.with(image)
                .load(feed.image)
                .dontTransform()
                .doOnEnd(::supportStartPostponedEnterTransition)
                .into(image)
        }
    }
}
