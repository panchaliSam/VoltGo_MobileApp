package lk.voltgo.voltgo.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import lk.voltgo.voltgo.data.local.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT COUNT(*) FROM user")
    suspend fun countUsers(): Int

    @Query("SELECT * FROM user WHERE user_id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM user WHERE nic = :nic")
    suspend fun getUserByNIC(nic: String): UserEntity?

    @Query("SELECT * FROM user WHERE user_id = :userId")
    fun observeUser(userId: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // no more updated_at column; just flip the flag
    @Query("UPDATE user SET is_active = 0 WHERE user_id = :userId")
    suspend fun deactivateUser(userId: String)

    // helper: update last login timestamp (ISO string)
    @Query("UPDATE user SET last_login_at = :isoTimestamp WHERE user_id = :userId")
    suspend fun updateLastLogin(userId: String, isoTimestamp: String)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()
}
