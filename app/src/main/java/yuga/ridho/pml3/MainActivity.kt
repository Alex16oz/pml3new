package yuga.ridho.pml3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import yuga.ridho.pml3.ui.theme.Pml3newTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Pml3newTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MahasiswaScreen()
                }
            }
        }
    }
}

@Composable
fun MahasiswaScreen() {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().getReference("TabelMahasiswa")

    var nim by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var mahasiswaList by remember { mutableStateOf<List<Mahasiswa>>(emptyList()) }

    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Mahasiswa>()
                for (dataSnapshot in snapshot.children) {
                    val mahasiswa = dataSnapshot.getValue(Mahasiswa::class.java)
                    mahasiswa?.let { list.add(it) }
                }
                mahasiswaList = list
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = nim,
            onValueChange = { nim = it },
            label = { Text("NIM") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Nama Mahasiswa") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = alamat,
            onValueChange = { alamat = it },
            label = { Text("Alamat") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (nim.isNotEmpty()) {
                    val mahasiswa = Mahasiswa(nim, nama, alamat)
                    database.child(nim).setValue(mahasiswa)
                    nim = ""
                    nama = ""
                    alamat = ""
                }
            }) {
                Text("Insert")
            }
            Button(onClick = {
                if (nim.isNotEmpty()) {
                    database.child(nim).removeValue()
                    nim = ""
                    nama = ""
                    alamat = ""
                }
            }) {
                Text("Delete")
            }
            Button(onClick = {
                if (nim.isNotEmpty()) {
                    val mahasiswa = Mahasiswa(nim, nama, alamat)
                    database.child(nim).setValue(mahasiswa)
                }
            }) {
                Text("Update")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(mahasiswaList) { mahasiswa ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "NIM: ${mahasiswa.nim}")
                        Text(text = "Nama: ${mahasiswa.namaMhs}")
                        Text(text = "Alamat: ${mahasiswa.alamatMhs}")
                    }
                }
            }
        }
    }
}