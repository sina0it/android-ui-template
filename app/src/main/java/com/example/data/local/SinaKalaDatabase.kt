package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.DiscountCodeEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.PromoBannerEntity
import com.example.data.model.RecentlyViewedEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WishlistItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        CartItemEntity::class,
        WishlistItemEntity::class,
        OrderEntity::class,
        UserProfileEntity::class,
        ReviewEntity::class,
        DiscountCodeEntity::class,
        PromoBannerEntity::class,
        RecentlyViewedEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SinaKalaDatabase : RoomDatabase() {

    abstract fun sinaKalaDao(): SinaKalaDao

    companion object {
        @Volatile
        private var INSTANCE: SinaKalaDatabase? = null

        fun getDatabase(context: Context): SinaKalaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SinaKalaDatabase::class.java,
                    "sinakala_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Prepopulate DB on first creation
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    val dao = database.sinaKalaDao()
                                    dao.insertCategories(SampleData.getInitialCategories())
                                    dao.insertBanners(SampleData.getInitialBanners())
                                    dao.insertProducts(SampleData.getInitialProducts())
                                    dao.insertOrUpdateProfile(SampleData.getInitialUserProfile())
                                    SampleData.getInitialDiscountCodes().forEach { dao.insertDiscountCode(it) }
                                    SampleData.getInitialReviews().forEach { dao.insertReview(it) }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
