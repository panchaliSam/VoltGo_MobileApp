/**
 * ------------------------------------------------------------
 * File: UserDao.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This interface defines the Data Access Object (DAO) for managing user-related database operations
 * in the VoltGo app. It includes methods for querying, inserting, updating, and deleting users,
 * as well as specialized operations like observing a user, deactivating an account, and updating login timestamps.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import lk.voltgo.voltgo.data.local.entities.UserEntity

@Dao
interface UserDao {
    // Returns the total number of users in the local database.
    @Query("SELECT COUNT(*) FROM user")
    suspend fun countUsers(): Int

    // Retrieves a user entity by its unique user ID.
    @Query("SELECT * FROM user WHERE user_id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    // Retrieves a user entity by email address.
    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    // Retrieves a user entity by NIC number.
    @Query("SELECT * FROM user WHERE nic = :nic")
    suspend fun getUserByNIC(nic: String): UserEntity?

    // Observes a user entity as a Flow for real-time UI updates.
    @Query("SELECT * FROM user WHERE user_id = :userId")
    fun observeUser(userId: String): Flow<UserEntity?>

    // Inserts a new user into the database; aborts if a conflict occurs.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    // Updates an existing user's details in the database.
    @Update
    suspend fun updateUser(user: UserEntity)

    // Deactivates a user by setting their 'is_active' flag to false.
    @Query("UPDATE user SET is_active = 0 WHERE user_id = :userId")
    suspend fun deactivateUser(userId: String)

    // Updates the 'last_login_at' timestamp for the given user.
    @Query("UPDATE user SET last_login_at = :isoTimestamp WHERE user_id = :userId")
    suspend fun updateLastLogin(userId: String, isoTimestamp: String)

    // Deletes a specific user record from the database.
    @Delete
    suspend fun deleteUser(user: UserEntity)

    // Deletes all user records from the database.
    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()
}
