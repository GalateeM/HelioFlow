package com.example.helioflow

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helioflow.placeholder.PlaceholderContent
import com.example.helioflow.placeholder.ShutterAction
import com.example.helioflow.placeholder.ShutterRule

class ShutterRulesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyShutterRulesRecyclerViewAdapter
    private val rulesList = PlaceholderContent.ITEMS.toMutableList()

    companion object {
        fun newInstance(): ShutterRulesFragment {
            return ShutterRulesFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.rule_item_list, container, false)

        recyclerView = view as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = MyShutterRulesRecyclerViewAdapter(rulesList)
        recyclerView.adapter = adapter

        return view
    }

    fun addNewRule() {
        showNewRuleDialog()
    }

    private fun showNewRuleDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_rule, null)
        val timeButton = dialogView.findViewById<View>(R.id.time_picker_button)
        
        var selectedHour = 8
        var selectedMinute = 0

        timeButton.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                    timeButton.tag = String.format("%02d:%02d", hour, minute)
                    (timeButton as? android.widget.Button)?.text = String.format("%02d:%02d", hour, minute)
                },
                selectedHour,
                selectedMinute,
                true
            ).show()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton(R.string.validate, null)
            .setNegativeButton(R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val actionRadioGroup = dialogView.findViewById<android.widget.RadioGroup>(R.id.action_radio_group)
                val action = if (actionRadioGroup.checkedRadioButtonId == R.id.radio_open) {
                    ShutterAction.OPEN
                } else {
                    ShutterAction.CLOSE
                }

                val days = mutableSetOf<Int>()
                if (dialogView.findViewById<CheckBox>(R.id.check_monday).isChecked) days.add(0)
                if (dialogView.findViewById<CheckBox>(R.id.check_tuesday).isChecked) days.add(1)
                if (dialogView.findViewById<CheckBox>(R.id.check_wednesday).isChecked) days.add(2)
                if (dialogView.findViewById<CheckBox>(R.id.check_thursday).isChecked) days.add(3)
                if (dialogView.findViewById<CheckBox>(R.id.check_friday).isChecked) days.add(4)
                if (dialogView.findViewById<CheckBox>(R.id.check_saturday).isChecked) days.add(5)
                if (dialogView.findViewById<CheckBox>(R.id.check_sunday).isChecked) days.add(6)

                if (days.isEmpty()) {
                    android.widget.Toast.makeText(context, R.string.no_day_selected, android.widget.Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newRule = ShutterRule(
                    id = (rulesList.size + 1).toString(),
                    action = action,
                    hour = selectedHour,
                    minute = selectedMinute,
                    days = days
                )

                rulesList.add(newRule)
                adapter.notifyItemInserted(rulesList.size - 1)
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}
