package com.example.helioflow

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.helioflow.placeholder.PlaceholderContent

/**
 * A fragment representing a list of Items.
 */
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
        val newItem = PlaceholderContent.PlaceholderItem(
            id = (rulesList.size + 1).toString(),
            content = "Nouvelle règle ${rulesList.size + 1}",
            details = "DETAILS"
        )

        rulesList.add(newItem)
        adapter.notifyItemInserted(rulesList.size - 1)
    }
}
