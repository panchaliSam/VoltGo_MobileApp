package lk.voltgo.voltgo.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import lk.voltgo.voltgo.data.local.entities.OperatorUserEntity

@Dao
interface OperatorUserDao {

    @Query("SELECT * FROM operator_user WHERE user_id = :userId")
    suspend fun getOperatorByUserId(userId: String): OperatorUserEntity?

    @Query("SELECT * FROM operator_user WHERE user_id = :userId")
    fun observeOperator(userId: String): Flow<OperatorUserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperator(operator: OperatorUserEntity)

    @Update
    suspend fun updateOperator(operator: OperatorUserEntity)

    @Delete
    suspend fun deleteOperator(operator: OperatorUserEntity)

    @Query("DELETE FROM operator_user")
    suspend fun deleteAllOperators()
}
