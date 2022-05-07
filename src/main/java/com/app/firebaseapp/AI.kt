package com.app.firebaseapp

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.*

class AI {
    private val phrases: Map<Regex, String> = mapOf(
        Regex(".*привет.*") to "Привет",
        Regex(".*как( у тебя| твои)? дела.*") to "Неплохо",
        Regex(".*(чем( ты)? занимаешься)|(что( ты)? делаешь).*") to "Отвечаю на вопросы",
        Regex(".*какой( сегодня)? день недели.*") to getDay(),
        Regex(".*какой сегодня день.*") to getDate(),
        Regex(".*(который( сейчас)? час)|(сколько( сейчас)? (времени|время)).*") to getTime()
    )

    fun getAnswer(question: String): String {
        val answers: ArrayList<String> = ArrayList()
        val qLowercase = question.lowercase()

        for (key in phrases.keys)
            if (key.containsMatchIn(qLowercase))
                answers.add(phrases[key]!!)

        if (Regex(".*сколько дней до.*").containsMatchIn(qLowercase))
            answers.add(getDaysBetween(qLowercase))

        if (answers.isEmpty())
            answers.add("Вопрос поняла. Думаю…")

        return answers.joinToString (", ")
    }

    private fun getDate(): String {
        val dateFormat = SimpleDateFormat("d.MM.yyyy", Locale.getDefault())
        return "Текущая дата - " + dateFormat.format(Date())
    }

    private fun getTime(): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return "Текущее время - " + dateFormat.format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDay(): String {
        val dateFormat = SimpleDateFormat("EEEE")
        return "Текущий день недели - " + dateFormat.format(Date())
    }

    private fun getDaysBetween(question: String): String {
        var strDate: String = question.substring(question.indexOf("дней до ") + 8)
        val end = strDate.indexOf('?')
        if (end != -1)
            strDate = strDate.substring(0, end)
        val dateFormat1 = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateFormat2 = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        var date: Date? = null

        try {
            date = dateFormat1.parse(strDate)
        } catch (e: Exception) { }

        if (date == null) {
            try {
                date = dateFormat2.parse(strDate)
            } catch (e: Exception) {
                return "Введена некорректная дата"
            }
        }

        val curDate = Date()
        val daysBetween: Int =
            ChronoUnit.DAYS.between(curDate.toInstant(), date!!.toInstant()).toInt()
        if (daysBetween < 0)
            return "Заданный вами день меньше текущего"
        else if (daysBetween == 0) {
            @Suppress("DEPRECATION")
            return if (curDate.day == date.day)
                "Этот день уже наступил"
            else
                "Этот день наступит завтра"
        }
        return "Осталось дней - $daysBetween"
    }
}