package uk.ac.tees.mad.d3617913

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val name: String
)