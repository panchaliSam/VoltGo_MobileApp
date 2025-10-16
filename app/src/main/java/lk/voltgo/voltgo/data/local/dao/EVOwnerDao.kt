/**
 * ------------------------------------------------------------
 * File: EVOwnerDao.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This interface defines the Data Access Object (DAO) for managing electric vehicle (EV) owner
 * data in the VoltGo app. It provides methods to retrieve, insert, update, and delete owner records
 * from the local Room database and supports observing owner changes reactively.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import lk.voltgo.voltgo.data.local.entities.EVOwnerEntity

@Dao
interface EVOwnerDao {

    // Retrieves a single EV owner record by its associated user ID.
    @Query("SELECT * FROM ev_owner WHERE user_id = :userId")
    suspend fun getEVOwnerByUserId(userId: String): EVOwnerEntity?

    // Observes changes to the EV owner entity for the specified user ID as a Flow.
    @Query("SELECT * FROM ev_owner WHERE user_id = :userId")
    fun observeEVOwner(userId: String): Flow<EVOwnerEntity?>

    // Inserts or replaces an EV owner entity in the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEVOwner(evOwner: EVOwnerEntity)

    // Updates an existing EV owner record in the database.
    @Update
    suspend fun updateEVOwner(evOwner: EVOwnerEntity)

    // Deletes a specific EV owner record from the database.
    @Delete
    suspend fun deleteEVOwner(evOwner: EVOwnerEntity)

    // Deletes all EV owner records from the database.
    @Query("DELETE FROM ev_owner")
    suspend fun deleteAllEVOwners()
}
