package com.example.Tinkoff_Kinopoisk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.Tinkoff_Kinopoisk.databinding.FragmentPopularBinding

class FragmentFavorites : Fragment() {
    private var binding: FragmentPopularBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPopularBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.buttonPopular.setOnClickListener {
            NavHostFragment.findNavController(this@FragmentFavorites)
                .navigate(R.id.action_FragmentFavorites_to_FragmentPopular)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}