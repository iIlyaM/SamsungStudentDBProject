package vsu.cs.samsungstudentdb.util

import android.app.AlertDialog
import android.content.Context
import java.util.concurrent.atomic.AtomicReference

object CrudUtils {
    fun showRadioButtonDialog(
        context: Context,
        title: String,
        items: List<String>,
        onItemSelected: (String) -> Unit
    ) {
        val selectedValue = AtomicReference<String>()

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setSingleChoiceItems(
            items.toTypedArray(),
            -1
        ) { _, which ->
            selectedValue.set(items[which])
        }
        builder.setPositiveButton("Выбрать") { _, _ ->
            val value = selectedValue.get()
            if (!value.isNullOrEmpty()) {
                onItemSelected(value)
            }
        }
        builder.setNegativeButton("Отмена", null)

        val dialog = builder.create()
        dialog.show()
    }

}