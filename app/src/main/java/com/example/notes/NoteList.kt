package com.example.notes

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.NoteAdapter
import com.example.notes.databinding.FragmentNoteListBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class NoteList : Fragment() {
    private lateinit var binding: FragmentNoteListBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var noteList: MutableList<NoteData> = mutableListOf()
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteListBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("Note_Data", MODE_PRIVATE)
        loadNotes()

        noteAdapter = NoteAdapter(noteList, onDelete = { position ->
            val deleteNote = noteList[position]
            noteList.removeAt(position)
            noteAdapter.notifyItemRemoved(position)
            saveNotes()
            Snackbar.make(binding.root, "Note Deleted ", Snackbar.LENGTH_LONG).setAction("Undo") {
                noteList.add(position, deleteNote)
                noteAdapter.notifyItemInserted(position)
                saveNotes()
            }.show()

        }, onNoteClick = { note: NoteData ->
            val fragment = NoteDetails()
            val bundle = Bundle().apply {
                putString("heading", note.Heading)
                putString("description", note.Description)
            }
            parentFragment?.setFragmentResult("noteDetails", bundle)
            findNavController().navigate(R.id.noteDetails, bundle)
        }
        )
        binding.rcv.layoutManager = LinearLayoutManager(requireContext())
        binding.rcv.adapter = noteAdapter
        return binding.root
    }

    private fun saveNotes() {
        val json = Gson().toJson(noteList)
        sharedPreferences.edit().putString("notes", json).apply()
    }

    private fun loadNotes() {
        val json = sharedPreferences.getString("notes", null)
        if (!json.isNullOrEmpty()) {
            val notesArray = Gson().fromJson(json, Array<NoteData>::class.java)
            noteList.clear()
            noteList.addAll(notesArray)
        }

    }

    override fun onResume() {
        super.onResume()
        readData()

    }

    private fun readData() {
        val json = sharedPreferences.getString("notes", null)
        val type = object : TypeToken<MutableList<NoteData>>() {}.type
        val updateList: MutableList<NoteData> =
            if (!json.isNullOrEmpty()) Gson().fromJson(json, type) else mutableListOf()
        noteList.clear()
        noteList.addAll(updateList)
        noteAdapter.notifyDataSetChanged()
    }
}