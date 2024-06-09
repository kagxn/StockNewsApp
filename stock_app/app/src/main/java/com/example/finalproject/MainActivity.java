package com.example.finalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.finalproject.databinding.MainActivityBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    private MainActivityBinding binding;
    private final int ID_PIYASALAR = 1;
    private final int ID_ANAMENU = 2;
    private final int ID_HABERLER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigation.add(new MeowBottomNavigation.Model(ID_PIYASALAR, R.drawable.stocks));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(ID_ANAMENU, R.drawable.home));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(ID_HABERLER, R.drawable.news));

        binding.bottomNavigation.show(ID_ANAMENU, true);

        binding.bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                return null;
            }
        });

        binding.bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                Fragment selectedFragment = null;
                if (model.getId() == 1) {
                    selectedFragment = new StockFragment();
                } else if (model.getId() == 2) {
                    selectedFragment = new HomeFragment();
                } else selectedFragment = new NewsFragment();

                replaceFragment(selectedFragment);

                return null;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment, null)
                .commit();
    }
}