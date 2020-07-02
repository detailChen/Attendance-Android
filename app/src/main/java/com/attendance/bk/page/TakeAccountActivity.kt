package com.attendance.bk.page

import am.drawable.CornerDrawable
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.adapter.BillTypeListAdapter
import com.attendance.bk.bean.net.TradeData
import com.attendance.bk.bus.BillTypeEvent
import com.attendance.bk.bus.BookSelEvent
import com.attendance.bk.bus.TradeEvent
import com.attendance.bk.db.*
import com.attendance.bk.db.table.Account
import com.attendance.bk.db.table.BillType
import com.attendance.bk.db.table.Book
import com.attendance.bk.db.table.Trade
import com.attendance.bk.dialog.AccountSelDialog
import com.attendance.bk.dialog.DatePickerDialog
import com.attendance.bk.page.billtype.ManageBillTypeActivity
import com.attendance.bk.utils.*
import com.attendance.bk.utils.Optional
import com.attendance.bk.view.RvDivider
import com.attendance.bk.view.numKeyboard.OnKeyboardListener
import com.blankj.utilcode.util.*
import com.bossbk.tablayout.QMUITabSegment
import com.bossbk.tablayout.SimpleOnTabSelectedListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jaeger.library.StatusBarUtil
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_take_account.*
import kotlinx.android.synthetic.main.activity_take_account.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * 记一笔页面
 * Created by Chen xuJie on 2020/4/19.
 */
class TakeAccountActivity : BaseActivity(), View.OnClickListener {


    private var mTrade: Trade? = null
    private var mBook: Book? = null
    private var isModify: Boolean = false

    private val mBookList = ArrayList<Book>()

    private var mBookTypePw: PopupWindow? = null

    private var mCurrTradeType = TRADE_TYPE_OUT

    private var mCurrTabSelPos = 0

    private var mBillTypeAdapter: BillTypeListAdapter? = null
//    private var mImageAdapter: ImageAdapter? = null

    private lateinit var mBottomBehaviorKb: BottomSheetBehavior<LinearLayout>
    private lateinit var mBottomBehaviorBt: BottomSheetBehavior<LinearLayout>

//    private var mImgList: MutableList<Image> = ArrayList(USER_VIP_IMG_MAX_NUM)

    private var mAccountSelDialog: AccountSelDialog? = null

    //    private var mProjectSelDialog: ProjectSelDialog? = null
    private var mDatePickerDialog: DatePickerDialog? = null

//    private var mTrader: Trader? = null//当前的付款人

    private val tradeTypeTabs = arrayListOf("支出", "收入")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_account)
        handleIntent(intent)
        initView()
        if (isModify) {
            initModify()
        } else {
            initDefData()
        }
        loadBooks()
        loadBookBtList(mTrade?.billId)
        addEBus()
    }


    private fun addEBus() {
        BkApp.eventBus.toUIObserver().`as`(bindLifecycle()).subscribe {
            if (it is BillTypeEvent) {
                if (it.type == BkDb.TYPE_DELETE && (it.billType?.billId == mTrade?.billId)) {
                    mTrade?.billId = ""//删除了
                }
                loadBookBtList(mTrade?.billId)
            }
        }
    }

    private fun handleIntent(intent: Intent) {
        mTrade = intent.getParcelableExtra(PARAM_TRADE)
        isModify = intent.getIntExtra(PARAM_OP_TYPE, -1) == BkDb.TYPE_MODIFY
        initBook()
    }

    private fun initBook() {
        val bookId = mTrade?.bookId ?: BkApp.currUser.getUserExtra().currBookId
        BkDb.instance.bookDao().queryForBookId(bookId)
            .`as`(bindLifecycle())
            .subscribe({ book ->
                mBook = book
            }, { throwable ->
                showToast("读取失败")
                LogUtils.e("queryForBookId failed->", throwable)
            })
    }

    private fun initView() {
        val toolbar = findViewById<RelativeLayout>(R.id.toolbar)
        toolbar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0)
        setSupportToolbar(toolbar)

        //確定是什么类型，应收款/应付款等等
        with(tab_title) {
            setDefaultSelectedColor(ColorUtils.getColor(R.color.white))
            setDefaultNormalColor(ColorUtils.getColor(R.color.white_dark))
            setTabTextSize(resources.getDimension(R.dimen.title_text_size).toInt())
            setItemSpaceInScrollMode(100)
            addOnTabSelectedListener(object : SimpleOnTabSelectedListener() {
                override fun onTabSelected(index: Int) {
                    mCurrTabSelPos = index
                    onTabSelect()
                }
            })
        }
        checkCurrTabSelPos()
        checkTradeType()

        bill_type_list.layoutManager = GridLayoutManager(this, 5)
        mBillTypeAdapter = BillTypeListAdapter(bill_type_list)
        bill_type_list.adapter = mBillTypeAdapter
        mBillTypeAdapter?.setOnItemClickListener(object :
            BillTypeListAdapter.OnItemClickListener<BillType> {
            override fun onItemClick(billType: BillType) {
                mBillTypeAdapter?.setSelPos(billType.billId)
                mTrade?.billId = billType.billId
                type_name.text = billType.name
                hideBillType()
                type_name.postDelayed({ expandedKeyBoard() }, 100)
            }

            override fun onAddClick() {
                if (BkApp.currUser.userIsVisitor()) {
                    BkUtil.showVisitorUserLoginDialog(this@TakeAccountActivity)
                    return
                }
                startActivity(ManageBillTypeActivity.startIntent(mBook!!.bookId, getBtType()))
            }
        })

        numKeyboard.setInputView(money_input, money_equation)
        money_input.resetSelection()
        numKeyboard.setKeyboardListener(object : OnKeyboardListener {
            override fun onSave() {
                onSaveClick(false)
            }

            override fun onAddAgain() {
                onSaveClick(true)
            }
        })

        //键盘
        val lpKb = bottom_sheet_kb.layoutParams as CoordinatorLayout.LayoutParams
        lpKb.behavior = BottomSheetBehavior<LinearLayout>()
        mBottomBehaviorKb = BottomSheetBehavior.from(bottom_sheet_kb)
        mBottomBehaviorKb.state = BottomSheetBehavior.STATE_EXPANDED

        //类别
        bottom_sheet_bt.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0)
        val lpBt = bottom_sheet_bt.layoutParams as CoordinatorLayout.LayoutParams
        lpBt.behavior = BottomSheetBehavior<LinearLayout>()
        mBottomBehaviorBt = BottomSheetBehavior.from(bottom_sheet_bt)
        mBottomBehaviorBt.state =
            if (isModify) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED

        type_name.setOnClickListener(this)
        money_input_layout.setOnClickListener(this)
        trade_time_layout.setOnClickListener(this)
        book_layout.setOnClickListener(this)
        pay_type_layout.setOnClickListener(this)

        BkUtil.limitEtTextInput(memo, 50)
        memo_click.setOnClickListener { v ->
            hideKeyBoard()
            v.postDelayed({ KeyboardUtils.showSoftInput(memo) }, 100)

        }
        KeyboardUtils.registerSoftInputChangedListener(this) {
            if (KeyboardUtils.isSoftInputVisible(this@TakeAccountActivity).not()) {
                memo.isCursorVisible = false
                memo_click.visibility = View.VISIBLE
                money_input_layout.postDelayed({ expandedKeyBoard() }, 200)
            } else {
                memo.isCursorVisible = true
                memo_click.visibility = View.GONE


            }
        }
    }

    private fun checkTradeType() {
        updateTabs(tradeTypeTabs)
        book.text = mBook?.name
        tab_title.notifyDataChanged()
        tab_title.selectTab(mCurrTabSelPos)
        onTabSelect()
    }


    private fun updateTabs(tabs: ArrayList<String>) {
        if (tab_title.adapter.size == 0) {
            tabs.forEach {
                tab_title.addTab(QMUITabSegment.Tab(it))
            }
        } else {
            for ((index, value) in tabs.withIndex()) {
                tab_title.replaceTab(index, QMUITabSegment.Tab(value))
            }
        }
    }

    private fun onTabSelect() {
        mTrade?.tradeType = if (mCurrTabSelPos == 0) TRADE_TYPE_OUT else TRADE_TYPE_IN
        mBillTypeAdapter?.reSetSelectPos()
        loadBookBtList(mTrade?.billId)
    }


    private fun checkCurrTabSelPos() {
        mCurrTabSelPos = if (isModify) {
            if (mTrade?.tradeType == TRADE_TYPE_OUT) 0 else 1
        } else {
            0
        }
    }

    private fun loadBookBtList(selBtId: String?) {
        BkDb.instance.billTypeDao().getBookBtList(BkApp.currUserId(), mBook!!.bookId, getBtType())
            .workerThreadChange().`as`(bindLifecycle())
            .subscribe({ btList ->
                mBillTypeAdapter?.updateData(btList)
                if (!TextUtils.isEmpty(selBtId)) {
                    mBillTypeAdapter?.setSelPos(selBtId)
                }
                var index = -1
                for (i in btList.indices) {
                    if (btList[i].billId == selBtId) {
                        index = i
                        break
                    }
                }
                if (index >= 0) {
                    type_name.text = btList[index].name
                } else {
                    type_name.text = ""
                    expandedBillType()
                }
            }, { e ->
                ToastUtils.showShort("读取失败")
                LogUtils.e("getBookBtList failed->", e)
            })
    }

    private fun initDefData() {
        newTrade()
        val date = DateUtil.currDate
        mTrade?.date = date
        trade_time.text = date
    }

    private fun newTrade() {
        mTrade = Trade(
            tradeId = UUIDUtil.uuid(),
            userId = BkApp.currUserId(),
            tradeType = TRADE_TYPE_OUT,
            bookId = mBook!!.bookId
        )
    }


    private fun initModify() {
        money_input.setText(BkUtil.formatMoney(mTrade!!.money))
        money_input.setSelection(money_input.length())
        trade_time.text = mTrade?.date
        memo.setText(mTrade?.memo)
        initAccount()
        initBook()
    }

    private fun initAccount() {
        mTrade?.accountId?.run {
            Single.create<Optional<Account>> {
                val account = BkDb.instance.accountDao().getAccountById(BkApp.currUserId(), this)
                if (account == null) {
                    it.onSuccess(Optional.empty())
                } else {
                    it.onSuccess(Optional.of(account))
                }
            }.workerThreadChange().`as`(bindLifecycle()).subscribe({
                if (it.isPresent) {
                    pay_type.text = it.get().name
                }
            }, {
                showToast("读取失败")
                LogUtils.e("initAccount failed->", it)
            })
        }
    }

    private fun expandedKeyBoard() {
        if (mBottomBehaviorKb.state != BottomSheetBehavior.STATE_EXPANDED) {
            mBottomBehaviorKb.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }


    private fun hideKeyBoard() {
        if (mBottomBehaviorKb.state != BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomBehaviorKb.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun expandedBillType() {
        if (mBottomBehaviorBt.state != BottomSheetBehavior.STATE_EXPANDED) {
            mBottomBehaviorBt.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun hideBillType() {
        if (mBottomBehaviorBt.state != BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomBehaviorBt.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }


    private fun getBtType(): Int {
        return if (mCurrTradeType == TRADE_TYPE_OUT) BILL_TYPE_OUT else BILL_TYPE_IN
    }


    private fun loadBooks() {
        BkDb.instance.bookDao().queryAllBook(BkApp.currUserId())
            .workerThreadChange().`as`(bindLifecycle()).subscribe({
                mBookList.clear()
                mBookList.addAll(it)
                if (it.size == 1) {
                    book_layout.isEnabled = false
                    ic_book.visibility = View.GONE
                } else {
                    book_layout.isEnabled = true
                    ic_book.visibility = View.VISIBLE
                }
            }, { LogUtils.e("loadBooks failed->", it) })
    }


    private fun onSaveClick(again: Boolean) {
        val sMoney = money_input.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(sMoney)) {
            showToast("金额不能为空哦")
            return
        }
        val money = java.lang.Double.valueOf(sMoney)
        if (money == 0.0) {
            showToast("金额不能为0哦")
            return
        }
        if (money < 0) {
            showToast("金额不能为负数哦")
            return
        }
        mTrade?.money = money

        if (mTrade?.bookId.isNullOrEmpty()) {
            showToast("请选择账本")
            return
        }
        mTrade?.memo = memo.text.toString().trim { it <= ' ' }

        if (BkApp.currUser.userIsVisitor()) {
            addVisitorUserTrade(again)
        } else {
            addModifyTrade(again)
        }
    }

    private fun addVisitorUserTrade(again: Boolean) {
        BkDb.instance.tradeDao().addVisitorUserTrade(mTrade!!)
            .workerThreadChange().`as`(bindLifecycle()).subscribe({
                if (it) {
                    if (again) {
                        showSuccessDialog()
                        resetData()
                        BkApp.eventBus.post(
                            TradeEvent(
                                mTrade,
                                if (isModify) BkDb.TYPE_MODIFY else BkDb.TYPE_ADD
                            )
                        )
                    } else {
                        showToast("添加成功")
                        BkApp.eventBus.post(
                            TradeEvent(
                                mTrade,
                                if (isModify) BkDb.TYPE_MODIFY else BkDb.TYPE_ADD
                            )
                        )
                        finish()
                    }
                }
            }, {
                showToast("添加失败")
                LogUtils.e("addVisitorUserTrade failed->", it)
            })
    }

    private fun addModifyTrade(again: Boolean) {

        if (!NetworkUtils.isConnected()) {
            showToast("请检查网络连接")
            return
        }
        val singleResult = if (isModify) {
            BkApp.apiService.updateTrade(TradeData(mTrade!!))
        } else {
            BkApp.apiService.addTrade(TradeData(mTrade!!))
        }

        singleResult.workerThreadChange().`as`(bindLifecycle()).subscribe({
            if (it.isResultOk()) {
                it.data?.apply {
                    BkDb.instance.tradeDao().addModifyTrade(trade, isModify)
//                    BkApp.ossService.asyncPutImageList(imageList)
                    if (again) {
                        showSuccessDialog()
                        resetData()
                        BkApp.eventBus.post(
                            TradeEvent(
                                trade,
                                if (isModify) BkDb.TYPE_MODIFY else BkDb.TYPE_ADD
                            )
                        )
                    } else {
                        showToast(if (isModify) "修改成功" else "添加成功")
                        BkApp.eventBus.post(
                            TradeEvent(
                                trade,
                                if (isModify) BkDb.TYPE_MODIFY else BkDb.TYPE_ADD
                            )
                        )
                        finish()
                    }
                }
            } else {
                showToast(it.desc)
            }
//            dismissDialog()
        }, {
            showToast(if (isModify) "修改失败" else "添加失败")
            LogUtils.e("addNormalTrade failed->", it)
//            dismissDialog()
        })
    }


    private fun showSuccessDialog() {
        val dialog = Dialog(this, R.style.dialog1)
        dialog.setContentView(R.layout.dialog_add_success)
        dialog.show()
        BkApp.mainHandler.postDelayed({ dialog.dismiss() }, 1000)
        BkApp.mainHandler.postDelayed({ expandedBillType() }, 1000)
    }

    private fun resetData() {
        //重新设置新的trade对象
        mTrade?.run {
            tradeId = UUIDUtil.uuid()
            billId = ""
            memo = null
            money = 0.0
            accountId = null
        }


        type_name.text = ""
        money_input.setText("")
        money_equation.setText("")
        memo.setText("")
        pay_type.text = ""
        mBillTypeAdapter?.setSelPos("")
    }


    private var bookListAdapter = object :
        BaseQuickAdapter<Book, BaseViewHolder>(R.layout.view_pop_book_type_list_item, mBookList) {
        override fun convert(helper: BaseViewHolder, item: Book) {
            helper.setText(R.id.book_type, item.name)
            helper.getView<View>(R.id.ic_book_sel).visibility =
                if (item.bookId == mSelBook?.bookId) View.VISIBLE else View.INVISIBLE
        }

        private var mSelBook: Book? = null
        fun setSelBook(book: Book?) {
            mSelBook = book
            notifyDataSetChanged()
        }
    }


    private fun showBookPw(anchor: View) {
        if (mBookTypePw == null) {
            val contentView = LayoutInflater.from(this).inflate(R.layout.view_pop_book_type, null)
            val bookTypeList = contentView.findViewById<RecyclerView>(R.id.book_type_list)
            bookTypeList.layoutManager = LinearLayoutManager(this)

            bookTypeList.adapter = bookListAdapter
            val divider = RvDivider()
            divider.skipLastDivider()
            bookTypeList.addItemDecoration(divider)
            mBookTypePw = if (bookListAdapter.itemCount > 6) {
                PopupWindow(contentView, ConvertUtils.dp2px(160f), ConvertUtils.dp2px(255f), true)
            } else {
                PopupWindow(
                    contentView,
                    ConvertUtils.dp2px(160f),
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
                )
            }
            bookListAdapter.setSelBook(mBook)

            val pwBg = CornerDrawable(
                Color.WHITE,
                ConvertUtils.dp2px(15f),
                ConvertUtils.dp2px(9f),
                0.25f, Gravity.TOP, Gravity.CENTER, 8.0f
            )
            contentView.background = pwBg

            bookListAdapter.setOnItemClickListener { _, _, position ->
                val book = bookListAdapter.getItem(position) ?: return@setOnItemClickListener
                if (book.bookId == mBook?.bookId) {
                    mBookTypePw?.dismiss()
                    return@setOnItemClickListener
                }
                mBookTypePw?.dismiss()
                changeCurrBook(book, bookListAdapter)

            }
            mBookTypePw?.isTouchable = true
            mBookTypePw?.isOutsideTouchable = true
            mBookTypePw?.setBackgroundDrawable(BitmapDrawable(null as Bitmap?))
        }

        BkUtil.dimBackground(this, 0.7f)
        mBookTypePw?.showAsDropDown(anchor, -50, ConvertUtils.dp2px(1f))
        mBookTypePw?.setOnDismissListener {
            BkUtil.dimBackground(this, 1f)
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }


    private fun changeCurrBook(book: Book, adapter: BaseQuickAdapter<Book, BaseViewHolder>) {
        val userExtra = BkApp.currUser.getUserExtra()
        userExtra.currBookId = book.bookId
        if (BkApp.currUser.userIsVisitor()) {
            if (BkApp.currUser.userIsVisitor()) {
                BkDb.instance.userExtraDao().updateUserExtra(userExtra).workerThreadChange()
                    .`as`(bindLifecycle()).subscribe({
                        mBook = book
                        mTrade?.bookId = book.bookId
                        bookListAdapter.setSelBook(book)
                        checkTradeType()
                        mBillTypeAdapter?.reSetSelectPos()
                        loadBookBtList(mTrade?.billId)
                        expandedBillType()
                        //通知账本页面切换账本
                        BkApp.eventBus.post(BookSelEvent(book))
                    }, {
                        showToast("切换账本失败")
                        LogUtils.e("updateCurrSelBook failed->", it)
                    })
            }
        } else {
            if (!NetworkUtils.isConnected()) {
                showToast("请检查网络连接")
                return
            }
//            showDialog("账本切换中，请稍后...")
            BkApp.apiService.updateUserExtra(userExtra).map<Optional<BookSelEvent>> {
                if (it.isResultOk()) {
                    if (it.data != null) {
                        BkDb.instance.userExtraDao().update(it.data)
                        Optional.of(BookSelEvent(book))
                    } else {
                        Optional.empty()
                    }
                } else {
                    showToast(it.desc)
                    Optional.empty()
                }
            }.workerThreadChange().`as`(bindLifecycle()).subscribe({
                it.opGet().apply {
                    mBook = book
                    mTrade?.bookId = book.bookId
                    findViewById<TextView>(R.id.book).text = mBook?.name
                    bookListAdapter.setSelBook(book)
//                    checkTradeType()
                    mBillTypeAdapter?.reSetSelectPos()
                    loadBookBtList(mTrade?.billId)
                    expandedBillType()
                    //通知账本页面切换账本
                    BkApp.eventBus.post(BookSelEvent(book))

                }
//                dismissDialog()
            }, {
                showToast("切换账本失败")
                LogUtils.e("updateCurrSelBook failed->", it)
//                dismissDialog()
            })
        }
    }


    private fun checkSoftInputVisible(): Boolean {
        return if (KeyboardUtils.isSoftInputVisible(this)) {
            KeyboardUtils.hideSoftInput(this)
            true
        } else {
            false
        }
    }

    private fun showDatePickerDialog() {
        if (mDatePickerDialog == null) {
            mDatePickerDialog = DatePickerDialog()
            mDatePickerDialog?.showAnimation(true)
            mDatePickerDialog?.setOnDateSelectedListener(object : DatePickerDialog.OnDateSelectedListener {
                override fun onDateSelect(year: Int, month: Int, day: Int) {
                    val cal = DateUtil.dayZeroTimeCal.apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day)
                    }

                    if (cal.after(DateUtil.dayZeroTimeCal)) {
                        showToast("不能大于当前时间哦")
                        return
                    }

                    val date = DateUtil.formatDate(cal.time)
                    mTrade?.date = date
                    trade_time.text = date
                }
            })
        }

        val cal = DateUtil.dayZeroTimeCal
        cal.time = DateUtil.parseDate(mTrade?.date)
        mDatePickerDialog?.setSelectedDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(
            Calendar.DAY_OF_MONTH))
        mDatePickerDialog?.show(supportFragmentManager, DatePickerDialog.TAG)
    }

    private fun showPayTypeDialog() {
        if (mAccountSelDialog == null) {
            mAccountSelDialog = AccountSelDialog()
            val bundle = Bundle()
            bundle.putString(AccountSelDialog.SEL_ACCOUNT_ID, mTrade?.accountId)
            mAccountSelDialog?.arguments = bundle
            mAccountSelDialog?.setOnAccountSelListener(object : AccountSelDialog.OnAccountSelListener {
                override fun onAccountSel(account: Account) {
                    mTrade?.accountId = account.accountId//先统一设置到该笔交易记录上，如果是应收应付款，再改变
                    pay_type.text = account.name
                }
            })
        } else {
            mAccountSelDialog?.setSelAccount(mTrade?.accountId)
        }
        mAccountSelDialog?.show(supportFragmentManager, AccountSelDialog.TAG)
    }



    override fun onClick(v: View) {
        when (v.id) {
            R.id.book_layout -> showBookPw(v)
            R.id.money_input_layout ->
                if (checkSoftInputVisible()) {
                    v.postDelayed({ expandedKeyBoard() }, 100)
                } else {
                    expandedKeyBoard()
                }
            R.id.type_name ->
                if (checkSoftInputVisible()) {
                    v.postDelayed({ expandedBillType() }, 100)
                } else {
                    expandedBillType()
                }
            R.id.trade_time_layout ->
                if (checkSoftInputVisible()) {
                    v.postDelayed({ showDatePickerDialog() }, 100)
                } else {
                    showDatePickerDialog()
                }
            R.id.pay_type_layout ->
                if (checkSoftInputVisible()) {
                    v.postDelayed({ showPayTypeDialog() }, 100)
                } else {
                    showPayTypeDialog()
                }
        }
    }


    override fun setSupportToolbar(toolbar: RelativeLayout) {
        StatusBarUtil.setColor(this, ColorUtils.getColor(R.color.text_third), 0)
        StatusBarUtil.setLightMode(this)
        toolbar.back.setOnClickListener { onBackPressed() }
    }


    companion object {


        const val PARAM_TRADE = "PARAM_TRADE"
        const val PARAM_OP_TYPE = "PARAM_OP_TYPE"

        /**
         * 从账本进来记账
         */
        fun startAddIntent(): Intent {
            return Intent(BkApp.appContext, TakeAccountActivity::class.java).also {
                it.putExtra(PARAM_OP_TYPE, BkDb.TYPE_ADD)
            }
        }

        /**
         * 从账本进来修改
         */
        fun startModifyIntent(trade: Trade): Intent {
            return Intent(BkApp.appContext, TakeAccountActivity::class.java).also {
                it.putExtra(PARAM_TRADE, trade)
                it.putExtra(PARAM_OP_TYPE, BkDb.TYPE_MODIFY)
            }
        }
    }


}