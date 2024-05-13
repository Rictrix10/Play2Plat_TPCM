import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.play2plat_tpcm.R

class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the TextView and set the HTML formatted text
        val textView = findViewById<TextView>(R.id.textView)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(getString(R.string.sign_up_prompt), Html.FROM_HTML_MODE_LEGACY)
        } else {
            textView.text = Html.fromHtml(getString(R.string.sign_up_prompt))
        }
    }
}
