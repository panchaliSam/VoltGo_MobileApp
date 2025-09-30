package lk.voltgo.voltgo.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import lk.voltgo.voltgo.data.local.entities.EVOwnerEntity

@Dao
interface EVOwnerDao {

    @Query("SELECT * FROM ev_owner WHERE user_id = :userId")
    suspend fun getEVOwnerByUserId(userId: String): EVOwnerEntity?

    @Query("SELECT * FROM ev_owner WHERE user_id = :userId")
    fun observeEVOwner(userId: String): Flow<EVOwnerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEVOwner(evOwner: EVOwnerEntity)

    @Update
    suspend fun updateEVOwner(evOwner: EVOwnerEntity)

    @Delete
    suspend fun deleteEVOwner(evOwner: EVOwnerEntity)

    @Query("DELETE FROM ev_owner")
    suspend fun deleteAllEVOwners()
}
