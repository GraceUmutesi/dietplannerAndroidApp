package rw.dietplanner.dietplanner.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_daily_meal_input.view.*
import kotlinx.android.synthetic.main.fragment_meal_planner.view.*
import kotlinx.android.synthetic.main.progress_layout.view.*
import rw.dietplanner.dietplanner.R

/**
 * A simple [Fragment] subclass.
 * Use the [MealPlannerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MealPlannerFragment : Fragment() {
    private lateinit var mealPlanNameView: EditText
    private lateinit var addDayBtn: Button
    private lateinit var createMealPlanBtn: Button
    private lateinit var progressLayout: RelativeLayout

    private lateinit var moBFSpinner: Spinner
    private lateinit var moLSpinner: Spinner
    private lateinit var moDSpinner: Spinner
    private lateinit var tuBFSpinner: Spinner
    private lateinit var tuLSpinner: Spinner
    private lateinit var tuDSpinner: Spinner
    private lateinit var weBFSpinner: Spinner
    private lateinit var weLSpinner: Spinner
    private lateinit var weDSpinner: Spinner
    private lateinit var thBFSpinner: Spinner
    private lateinit var thLSpinner: Spinner
    private lateinit var thDSpinner: Spinner
    private lateinit var frBFSpinner: Spinner
    private lateinit var frLSpinner: Spinner
    private lateinit var frDSpinner: Spinner
    private lateinit var saBFSpinner: Spinner
    private lateinit var saLSpinner: Spinner
    private lateinit var saDSpinner: Spinner
    private lateinit var suBFSpinner: Spinner
    private lateinit var suLSpinner: Spinner
    private lateinit var suDSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meal_planner, container, false)

        mealPlanNameView = view.meal_plan_name_view
        addDayBtn = view.add_day_btn
        createMealPlanBtn = view.create_meal_plan_btn
        progressLayout = view.progress_bar

        moBFSpinner = view.mo_breakfast_spinner
        moLSpinner = view.mo_lunch_spinner
        moDSpinner = view.mo_dinner_spinner
        tuBFSpinner = view.tu_breakfast_spinner
        tuLSpinner = view.tu_lunch_spinner
        tuDSpinner = view.tu_dinner_spinner
        weBFSpinner = view.we_breakfast_spinner
        weLSpinner = view.we_lunch_spinner
        weDSpinner = view.we_dinner_spinner
        thBFSpinner = view.th_breakfast_spinner
        thLSpinner = view.th_lunch_spinner
        thDSpinner = view.th_dinner_spinner
        frBFSpinner = view.fr_breakfast_spinner
        frLSpinner = view.fr_lunch_spinner
        frDSpinner = view.fr_dinner_spinner
        saBFSpinner = view.sa_breakfast_spinner
        saLSpinner = view.sa_lunch_spinner
        saDSpinner = view.sa_dinner_spinner
        suBFSpinner = view.su_breakfast_spinner
        suLSpinner = view.su_lunch_spinner
        suDSpinner = view.su_dinner_spinner

        return view
    }
}