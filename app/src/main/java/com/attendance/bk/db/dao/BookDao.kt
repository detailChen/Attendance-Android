package com.attendance.bk.db.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.Book
import io.reactivex.Single

/**
 * Created by Chen xuJie on 2018/12/17/017
 */
@Dao
abstract class BookDao {

    @Insert
    abstract fun insert(book: Book)

    @Insert
    abstract fun insert(bookList: List<Book>)

    @Update
    abstract fun update(book: Book)

    @Update
    abstract fun update(bookList: List<Book>)


    @Query("select * from tb_book where book_id = :bookId and operator_type != 2")
    abstract fun queryForBookId(bookId: String?): Single<Book>


    //查询所有的账本
    @Query("select * from tb_book where user_id= :userId and operator_type != 2 order by order_num")
    abstract fun queryAllBook(userId: String): Single<List<Book>>


    //查询所有的某个账本类型的账本，如所有的收入账本或支出账本
    @Query("select * from tb_book where user_id= :userId and book_type_id =:bookTypeId and operator_type != 2 order by order_num")
    abstract fun queryAllSameTypeBook(userId: String, bookTypeId: String): Single<List<Book>>


    //查询最大顺序
    @Query("select max(order_num) from tb_book where user_id = :userId and operator_type != 2")
    abstract fun getMaxOrder(userId: String): Int

    @Query("select * from tb_book where user_id = :userId and book_id != :selfBookId and name =:name and operator_type != 2")
    abstract fun querySameNameBook(userId: String,selfBookId: String, name: String): Book?


    fun saveOrder(bookList: List<Book>?) {
        BkDb.instance.runInTransaction {
            bookList?.apply { update(this) }
        }
    }




}
