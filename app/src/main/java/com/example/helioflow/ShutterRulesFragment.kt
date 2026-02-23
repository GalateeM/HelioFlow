package com.example.helioflow

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helioflow.api.ShutterApiClient
import com.example.helioflow.api.parseProgrammations
import com.example.helioflow.api.toCreateRequest
import com.example.helioflow.api.toShutterRule
import com.example.helioflow.placeholder.PlaceholderContent
import com.example.helioflow.placeholder.ShutterAction
import com.example.helioflow.placeholder.ShutterRule
import kotlinx.coroutines.launch

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

        adapter = MyShutterRulesRecyclerViewAdapter(
            rulesList,
            onEditClick = { rule, _ -> showEditRuleDialog(rule) },
            onDeleteClick = { rule, _ -> confirmDeleteRule(rule) }
        )
        recyclerView.adapter = adapter

        fetchProgrammations()

        return view
    }

    private fun fetchProgrammations() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val json = ShutterApiClient.instance.getProgrammationsRaw()
                val programmations = parseProgrammations(json)
                rulesList.clear()
                rulesList.addAll(programmations.map { it.toShutterRule() })
                if (::adapter.isInitialized) {
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erreur lors du chargement: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addNewRule() {
        if (!::adapter.isInitialized) {
            Toast.makeText(context, "Chargement en cours...", Toast.LENGTH_SHORT).show()
            return
        }
        showRuleDialog(null)
    }

    private fun showEditRuleDialog(rule: ShutterRule) {
        showRuleDialog(rule)
    }

    private fun confirmDeleteRule(rule: ShutterRule) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteRule(rule)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteRule(rule: ShutterRule) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                ShutterApiClient.instance.deleteProgrammation(rule.id.toInt())
                val position = rulesList.indexOfFirst { it.id == rule.id }
                if (position != -1) {
                    rulesList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
                Toast.makeText(context, R.string.rule_deleted, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Erreur lors de la suppression: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRuleDialog(existingRule: ShutterRule?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_rule, null)
        val timeButton = dialogView.findViewById<View>(R.id.time_picker_button)
        
        var selectedHour = existingRule?.hour ?: 8
        var selectedMinute = existingRule?.minute ?: 0
        val isEdit = existingRule != null

        if (isEdit) {
            timeButton.tag = String.format("%02d:%02d", selectedHour, selectedMinute)
            (timeButton as? android.widget.Button)?.text = String.format("%02d:%02d", selectedHour, selectedMinute)
            
            val actionRadioGroup = dialogView.findViewById<android.widget.RadioGroup>(R.id.action_radio_group)
            if (existingRule.action == ShutterAction.OPEN) {
                actionRadioGroup.check(R.id.radio_open)
            } else {
                actionRadioGroup.check(R.id.radio_close)
            }
            
            dialogView.findViewById<CheckBox>(R.id.check_monday).isChecked = 0 in existingRule.days
            dialogView.findViewById<CheckBox>(R.id.check_tuesday).isChecked = 1 in existingRule.days
            dialogView.findViewById<CheckBox>(R.id.check_wednesday).isChecked = 2 in existingRule.days
            dialogView.findViewById<CheckBox>(R.id.check_thursday).isChecked = 3 in existingRule.days
            dialogView.findViewById<CheckBox>(R.id.check_friday).isChecked = 4 in existingRule.days
            dialogView.findViewById<CheckBox>(R.id.check_saturday).isChecked = 5 in existingRule.days
            dialogView.findViewById<CheckBox>(R.id.check_sunday).isChecked = 6 in existingRule.days
        } else {
            timeButton.tag = String.format("%02d:%02d", selectedHour, selectedMinute)
            (timeButton as? android.widget.Button)?.text = String.format("%02d:%02d", selectedHour, selectedMinute)
        }

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
            .setTitle(if (isEdit) R.string.edit_rule_title else R.string.new_rule_title)
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

                val rule = ShutterRule(
                    id = existingRule?.id ?: ((rulesList.maxOfOrNull { it.id.toIntOrNull() ?: 0 } ?: 0) + 1).toString(),
                    action = action,
                    hour = selectedHour,
                    minute = selectedMinute,
                    days = days
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        if (isEdit) {
                            ShutterApiClient.instance.updateProgrammation(rule.id.toInt(), rule.toCreateRequest())
                            val position = rulesList.indexOfFirst { it.id == existingRule.id }
                            if (position != -1) {
                                rulesList[position] = rule
                                adapter.notifyItemChanged(position)
                            }
                            Toast.makeText(context, R.string.rule_updated, Toast.LENGTH_SHORT).show()
                        } else {
                            ShutterApiClient.instance.createProgrammation(rule.toCreateRequest())
                            rulesList.add(rule)
                            adapter.notifyItemInserted(rulesList.size - 1)
                        }
                        dialog.dismiss()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                        throw e;
                    }
                }
            }
        }

        dialog.show()
    }
}
