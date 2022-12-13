package com.example.topicreview.database

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ReviewDao {

    @Query("SELECT * FROM category")
    abstract fun getCategories(): Flow<List<Category>>

    @Query("SELECT * FROM topic")
    abstract fun getTopics(): Flow<List<Topic>>

    @Query("SELECT * FROM topic WHERE date_due <= :currentDate ORDER BY title ASC")
    protected abstract fun getDueTopicsAsc(currentDate: Long): Flow<List<Topic>>
    @Query("SELECT * FROM topic WHERE date_due <= :currentDate ORDER BY title DESC")
    protected abstract fun getDueTopicsDesc(currentDate: Long): Flow<List<Topic>>

    @Query("SELECT * FROM topic WHERE id = :id")
    abstract fun getTopic(id: Int): Flow<Topic>

    @Insert(onConflict = IGNORE)
    abstract suspend fun insertTopic(topic: Topic)

    @Insert(onConflict = IGNORE)
    abstract suspend fun insertTopics(topics: List<Topic>)

    @Update
    abstract suspend fun updateTopic(topic: Topic)

    @Delete
    abstract suspend fun deleteTopic(topic: Topic)

    @Insert(onConflict = IGNORE)
    abstract suspend fun insertCategory(category: Category)

    @Update
    abstract suspend fun updateCategory(category: Category)

    @Query("SELECT * FROM category WHERE id = :id")
    abstract fun getCategory(id: Int): Flow<Category>

    @Query("SELECT * FROM category WHERE id <> :id")
    abstract fun getCategoriesExcept(id: Int): Flow<List<Category>>

    @Query("DELETE FROM category WHERE id = :id")
    abstract suspend fun deleteCategory(id: Int)

    fun getDueTopics(currentDate: Long, sorting: Sorting): Flow<List<Topic>> {
        return when(sorting) {
            Sorting.A_TO_Z -> getDueTopicsAsc(currentDate)
            Sorting.Z_TO_A -> getDueTopicsDesc(currentDate)
        }
    }
}