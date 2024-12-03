package com.example.dz13sqlitepractice1

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var toolbarMain:Toolbar

    var products: MutableList<Product> = mutableListOf()
    var listAdapter: CustomListAdapter? = null
    val dataBase = DBHelper(this)
    var updateId: Int = 1

    private lateinit var productIdTV: TextView

    private lateinit var listViewLV: ListView

    private lateinit var saveBTN: Button
    private lateinit var updateBTN: Button
    private lateinit var deleteBTN: Button

    private lateinit var productNameET: EditText
    private lateinit var productWeightET: EditText
    private lateinit var productPriceET: EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Тулбар
        toolbarMain = findViewById(R.id.toolbarMain)
        setSupportActionBar(toolbarMain)
        title = "  Потребительская "
        toolbarMain.subtitle = "корзина"
        toolbarMain.setLogo(R.drawable.shop)

        //Привязываем кнопки
        productIdTV = findViewById(R.id.productIdTV)
        productNameET = findViewById(R.id.productNameET)
        productWeightET = findViewById(R.id.productWeightET)
        productPriceET = findViewById(R.id.productPriceET)
        listViewLV = findViewById(R.id.listViewLV)
        saveBTN = findViewById(R.id.saveBTN)
        updateBTN = findViewById(R.id.updateBTN)
        deleteBTN = findViewById(R.id.deleteBTN)

        //заполняем список на экране активности
        viewDataAdapter()
        updateIdInScreen()

        //Обработка кнопки Сохранить
        saveBTN.setOnClickListener{
            saveRecord()
        }
    }

    override fun onResume() {
        super.onResume()
        //Обработка кнопки Редактировать
        updateBTN.setOnClickListener {
            updateRecord()
        }
        //обработка кнопки Удалить
        deleteBTN.setOnClickListener {
            deleteRecord()
        }
    }

    private fun updateRecord() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        //применяем приготовленй шаблон
        val dialogView = inflater.inflate(R.layout.dialog_update, null)
        dialogBuilder.setView(dialogView)
        //привязываем поля в диалоге
        val editId = dialogView.findViewById<EditText>(R.id.updateIdET)
        val editName = dialogView.findViewById<EditText>(R.id.updateNameET)
        val editWeight = dialogView.findViewById<EditText>(R.id.updateWeightET)
        val editPrice = dialogView.findViewById<EditText>(R.id.updatePriceET)
        //Заголовок Диалога и сообщение информационное
        dialogBuilder.setTitle("Обновить запись")
        dialogBuilder.setMessage("Введите данные")
        //Кнопка Редактировать Диалога
        dialogBuilder.setPositiveButton("Обновить") { _, _ ->
            updateId = editId.text.toString().toInt()
            val updateName = editName.text.toString()
            val updateWeight = editWeight.text.toString()
            val updatePrice = editPrice.text.toString()
            //Проверяем на Валидность
            if (updateId.toString().trim() != "" && updateName.trim() != "" && updateWeight.trim() != "" && updatePrice.trim() != "") {
                //Создаем новый Продукт из введеных данных
                val product = Product(updateId, updateName, updateWeight, updatePrice)
                //Перезаписываем в БД
                dataBase.updateProduct(product)
                //Обновляем экран
                viewDataAdapter()
                updateIdInScreen()
                //Выкидываем сообщение
                Toast.makeText(applicationContext, "Запись обновлена", Toast.LENGTH_SHORT).show()
            }
        }
        //Кнопка отмена диалога
        dialogBuilder.setNegativeButton("Отмена") {dialog, which -> }
        //Собрать диалог и показать
        dialogBuilder.create().show()
    }

    //Функция Удалить запись и инициация диалога Удаление записи
    private fun deleteRecord() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_delete, null)
        dialogBuilder.setView(dialogView)
        val chooseDeleteId = dialogView.findViewById<EditText>(R.id.deleteIdET)
        dialogBuilder.setTitle("Удалить запись")
        dialogBuilder.setMessage("Введите id")
        //Кнопка Удалить Диалога
        dialogBuilder.setPositiveButton("Удалить") { _, _ ->
            val deleteId = chooseDeleteId.text.toString()
            if (deleteId.trim() != "") {
                val product = Product(Integer.parseInt(deleteId), "", "", "")
                //Удаляем
                dataBase.deleteProduct(product)
                //Обновляем
                viewDataAdapter()
                updateIdInScreen()
                //Выкидываем Сообщение
                Toast.makeText(applicationContext, "Запись удалена", Toast.LENGTH_SHORT).show()
            }
        }
        //Кнопка отмена Диалога
        dialogBuilder.setNegativeButton("Отмена") { _, _ -> }
        //Диалог Создать и показать
        dialogBuilder.create().show()
    }

    //Функция Сохранить
    private fun saveRecord() {
        //Если база пустая то Id 1
        if (products.isNotEmpty()) { updateId }
        else updateId = 1
        //Присваиваем значения
        val name = productNameET.text.toString()
        val weight = productWeightET.text.toString()
        val price = productPriceET.text.toString()
        if (updateId.toString() != "" && name.trim() != "" && weight.trim() != "" && price.trim() != "") {
            //Создаем Продукт
            val product = Product(updateId, name, weight, price)
            //Добавляем в базу
            products.add(product)
            dataBase.addProduct(product)
            //Выкидываем сообщение
            Toast.makeText(applicationContext, "Запись добавлена", Toast.LENGTH_SHORT).show()
            //Очищаем поля
            productIdTV.text = ""
            productNameET.text.clear()
            productWeightET.text.clear()
            productPriceET.text.clear()
            //Увеличиваем Id
            updateId++
            viewDataAdapter()
        }
    }

    //Инициализация Меню
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.infoMenuMain -> {
                Toast.makeText(
                    applicationContext, "Автор Ефремов О.В. Создан 3.12.2024",
                    Toast.LENGTH_LONG
                ).show()
            }

            R.id.exitMenuMain -> {
                Toast.makeText(
                    applicationContext, "Работа приложения завершена",
                    Toast.LENGTH_LONG
                ).show()
                finishAffinity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun viewDataAdapter() {
        products = dataBase.readProduct()
        listAdapter = CustomListAdapter(this, products)
        listViewLV.adapter = listAdapter
        listAdapter?.notifyDataSetChanged()
        productIdTV.text = updateId.toString()
    }

    private fun updateIdInScreen() {
        updateId = if (products.isNotEmpty()) {
            products[products.size - 1].productId + 1
        } else {
            1 // Если список пуст, начинаем с ID = 1
        }
        productIdTV.text = updateId.toString()
    }

}