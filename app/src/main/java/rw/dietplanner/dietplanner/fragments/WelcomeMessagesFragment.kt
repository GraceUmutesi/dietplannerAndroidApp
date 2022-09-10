package rw.dietplanner.dietplanner.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_welcome_messages.view.*
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.activities.WelcomeActivity
import rw.dietplanner.dietplanner.utils.Constants
import rw.dietplanner.dietplanner.utils.Constants.WELCOME_PAGE_NUMBER

class WelcomeMessagesFragment : Fragment() {

    private var pagePos: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_welcome_messages, container, false)

        pagePos = requireArguments().getInt(WELCOME_PAGE_NUMBER)

        val pageCaption = requireArguments().getString(Constants.WELCOME_PAGE_CAPTION)
        val pageImageId = requireArguments().getInt(Constants.WELCOME_PAGE_IMAGE_ID)

        view.image_caption.text = pageCaption
        Picasso.get().load(pageImageId).into(view.recipe_image)

        return view
    }

    override fun onResume() {
        super.onResume()

        (activity as WelcomeActivity).setDotsLayout(pagePos)
    }

}