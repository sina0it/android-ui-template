package com.example.ui.components

import java.text.DecimalFormat

object PersianUtils {

    fun toPersianDigits(input: String): String {
        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val builder = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                builder.append(persianDigits[ch - '0'])
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    fun toPersianDigits(number: Number): String {
        return toPersianDigits(number.toString())
    }

    fun formatPriceToman(amount: Long): String {
        val formatter = DecimalFormat("#,###")
        val formatted = formatter.format(amount)
        return "${toPersianDigits(formatted)} تومان"
    }

    fun formatPriceToman(amount: Double): String {
        return formatPriceToman(amount.toLong())
    }

    fun formatDiscount(percent: Int): String {
        return "٪${toPersianDigits(percent)}"
    }

    fun formatRating(rating: Double): String {
        return toPersianDigits(String.format("%.1f", rating))
    }

    fun formatTimeRemaining(secondsTotal: Long): Triple<String, String, String> {
        val hours = secondsTotal / 3600
        val minutes = (secondsTotal % 3600) / 60
        val seconds = secondsTotal % 60

        val hStr = toPersianDigits(String.format("%02d", hours))
        val mStr = toPersianDigits(String.format("%02d", minutes))
        val sStr = toPersianDigits(String.format("%02d", seconds))
        return Triple(hStr, mStr, sStr)
    }
}
