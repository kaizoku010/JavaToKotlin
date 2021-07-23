package com.sriyank.javatokotlindemo.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputLayout
import com.sriyank.javatokotlindemo.R
import com.sriyank.javatokotlindemo.app.Constants
import com.sriyank.javatokotlindemo.app.isNotEmpty
import kotlinx.android.synthetic.main.activity_main.*;


class MainActivity : AppCompatActivity() {
    companion object{
        private val TAG
        : String = MainActivity::class.java.simpleName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);


          setSupportActionBar(toolbar)

          

      }

    fun saveName(view: View) {

if (etName.isNotEmpty(inputLayoutName)){


        val personName =etName.text.toString();
        val sp = getSharedPreferences(Constants
                .APP_SHARED_PREFERENCES, Context
                .MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(Constants.KEY_PERSON_NAME, personName)
     editor.apply()
}
}

    fun listUserRepositories(view: View) {

    if (etGithubUser.isNotEmpty(inputLayoutGithubUser)){

        val githubUSer = etGithubUser.text.toString()
        val intent = Intent(this@MainActivity,
                DisplayActivity::class.java)

        intent.putExtra(Constants.KEY_QUERY_TYPE,
                Constants.SEARCH_BY_USER)
        intent.putExtra(Constants.KEY_GITHUB_USER, githubUSer)

        startActivity(intent)

    }
    }
    fun  listRepositories(view: View){

        if (etRepoName.isNotEmpty(inputLayoutRepoName)) {

            val queryRepo = etRepoName.text.toString()
            val language = etLanguage.text.toString()

            val intent = Intent(this@MainActivity,
                    DisplayActivity::class.java);
            intent.putExtra(Constants.KEY_QUERY_TYPE, Constants.SEARCH_BY_REPO)
            intent.putExtra(Constants.KEY_REPO_SEARCH, queryRepo)
            intent.putExtra(Constants.KEY_LANGUAGE, language)
            startActivity(intent)

        }
    }

    }


