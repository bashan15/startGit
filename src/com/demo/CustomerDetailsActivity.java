package com.manyi.saleagent.fragment.customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.huoqiu.framework.app.AppConfig;
import com.huoqiu.framework.commhttp.Response;
import com.huoqiu.framework.util.CheckDoubleClick;
import com.huoqiu.framework.util.DialogBuilder;
import com.huoqiu.framework.util.GeneratedClassUtils;
import com.huoqiu.framework.util.StringUtil;
import com.huoqiu.framework.util.ToastUtil;
import com.huoqiu.framework.widget.IconFontView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.manyi.saleagent.GuideActivity;
import com.manyi.saleagent.MainActivity;
import com.manyi.saleagent.R;
import com.manyi.saleagent.cachebean.user.AgentToUserMessageExt;
import com.manyi.saleagent.cachebean.user.CheckUserLimitRequest;
import com.manyi.saleagent.cachebean.user.CheckUserLimitResponse;
import com.manyi.saleagent.cachebean.user.CustomerDetailsData;
import com.manyi.saleagent.cachebean.user.CustomerDetailsRequest;
import com.manyi.saleagent.cachebean.user.CustomerDetailsResponse;
import com.manyi.saleagent.cachebean.user.EditUserClassRequest;
import com.manyi.saleagent.cachebean.user.EditUserClassResponse;
import com.manyi.saleagent.cachebean.user.GetAgentRecommendUserAppTipsRequest;
import com.manyi.saleagent.cachebean.user.GetAgentRecommendUserAppTipsResponse;
import com.manyi.saleagent.cachebean.user.GetUserAuthorizeRequest;
import com.manyi.saleagent.cachebean.user.GetUserAuthorizeResponse;
import com.manyi.saleagent.cachebean.user.SendAgentRecommendUserAppSmsRequest;
import com.manyi.saleagent.cachebean.user.UserRequirementDataWithId;
import com.manyi.saleagent.cachebean.user.UserSecretPhoneResponse;
import com.manyi.saleagent.cachebean.user.hx.GetHXUserInfoRequest;
import com.manyi.saleagent.cachebean.user.hx.HXUserResponse;
import com.manyi.saleagent.cachebean.user.hx.WeakHxUserInfo;
import com.manyi.saleagent.chat.ChatPresenter;
import com.manyi.saleagent.chat.ui.ChatActivity;
import com.manyi.saleagent.chat.utils.ChatUtil;
import com.manyi.saleagent.common.Constants;
import com.manyi.saleagent.common.SaleAgentBaseActivity;
import com.manyi.saleagent.common.SaleAgentHttpListener;
import com.manyi.saleagent.common.util.DialogManager;
import com.manyi.saleagent.common.util.PhoneNoUtil;
import com.manyi.saleagent.common.util.PreferenceUtil;
import com.manyi.saleagent.common.util.SendAuthorizeHelper;
import com.manyi.saleagent.common.util.UserInfoUtil;
import com.manyi.saleagent.fragment.agenda.AgendaCreateActivity;
import com.manyi.saleagent.fragment.agenda.ScheduleDetailsActivity;
import com.manyi.saleagent.fragment.customer.adapter.CustomerDetailsPagerAdapter;
import com.manyi.saleagent.fragment.customer.util.UserIdentityHelper;
import com.manyi.saleagent.fragment.house.HouseInfoActivity;
import com.manyi.saleagent.fragment.house.RecommendGuideActivity;
import com.manyi.saleagent.fragment.source.HousesSourceActivity;
import com.manyi.saleagent.presenter.customer.CustomerDetailsPresenter;
import com.manyi.saleagent.provider.DisableChatDBUtil;
import com.manyi.saleagent.reqsaction.CustomerReqsAction;
import com.manyi.saleagent.reqsaction.UserReqsAction;
import com.manyi.saleagent.widget.ClientDetailScrollView;
import com.manyi.saleagent.widget.viewpageindicator.IndexViewPager;
import com.manyi.saleagent.widget.viewpageindicator.UnderlinePageIndicator;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apmem.tools.layouts.FlowLayout;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import de.greenrobot.event.EventBus;

/**
 * Created by jason on 2015/1/13.
 */
@EActivity(R.layout.activity_customer_details)
public class CustomerDetailsActivity extends SaleAgentBaseActivity {
    @ViewById(R.id.scroll_view)
    ClientDetailScrollView mScrollView;

    @ViewById(R.id.title_text)
    TextView title_txt;
    @ViewById(R.id.title_text2)
    TextView title_txt2;
    @ViewById(R.id.textbtn_add)
    TextView mtextbtn_add;
    @ViewById(R.id.client_name)
    TextView mClientName; //名字
    @ViewById(R.id.ll_edit)
    View ll_edit;//编辑
    @ViewById(R.id.client_demand)
    TextView mClientDemand; //客户需求
    @ViewById(R.id.client_remarks)
    TextView client_remarks;//备注
    @ViewById(R.id.tv_app_name)
    TextView tv_app_name;//APP用户名
    @ViewById(R.id.ll_appname)
    LinearLayout ll_appname;
    @ViewById(R.id.ll_remarks)
    LinearLayout ll_remarks;

    @ViewById(R.id.client_hidden_phone)
    TextView mClientHiddenPhone; //客户电话
    @ViewById(R.id.client_phone)
    TextView mClientPhone; //客户电话
    @ViewById(R.id.iv_custom_type)
    ImageView mIVCustomerType;//客户类型
    @ViewById(R.id.distribute_house)
    Button distribute_house;//配盘

    @ViewById(R.id.activity_customer_txt_first_intention)
    TextView txtFirstIntention; //客户电话

    private final static int REQUEST_STATE_ALL = 0;
    private final static int REQUEST_STATE_WILL = 1;
    private final static int REQUEST_STATE_LOOKED = 2;
    private final static int REQUEST_STATE_NO = 3;
    private static final int REQUEST_ADD_DISTRIBUTE = 4;//手动配盘
    /**
     * 是否是初始化tab
     */
    private boolean isInitTab = true;
    @Extra
    static Long mUserId;
    @Extra
    boolean isCustomDeal;//是否是签单客户
    @Extra("currentTab")
    int mCurrentTab;
    private static final int REQUEST_ACTION_EDIT_CUSTOMER = 10;
    public static final int REQUEST_ACTION_ADD_MEMO = 11;
    private CustomerDetailsResponse mCustomerDetailsResponse;
    private String source = null;
    private UserRequirementDataWithId userRequirementData;
    private CustomerDetailsPresenter detailsController;

    public static final String TAG_ALL = "tag_all";
    public static final String TAG_WILL = "tag_will";
    public static final String TAG_LOOKED = "tag_looked";
    public static final String TAG_NONE = "tag_none";

    public static final String EXTRA_REQUEST_STATE = "mRequestState";

    private int mSelectedPage = 0;

    @ViewById(R.id.pager)
    IndexViewPager mViewPager;

    @ViewById(R.id.indicator_view)
    UnderlinePageIndicator mIndicator;

    @ViewById(R.id.tabhost)
    TabHost mTabHost;

    CustomerDetailsPagerAdapter attentionPagerAdapter;

    View[] views = new View[4];

    //在线聊天图标
    @ViewById(R.id.if_chat)
    IconFontView ifChat;
    //在线聊天点击区域视图
    @ViewById(R.id.online_chat)
    LinearLayout llChatOnline;
    //打电话点击区域视图
    @ViewById(R.id.call_phone_layout)
    LinearLayout llCall;
    //打电话图标
    @ViewById(R.id.call_phone_icon)
    IconFontView ifCall;
    //发短信点击区域
    @ViewById(R.id.send_sms_top)
    LinearLayout llSendSMS;
    //发短信图标
    @ViewById(R.id.if_sms)
    IconFontView ifSMS;

    @ViewById(R.id.tv_unread_msg_count)
    TextView tvUnreadMsgCount;

    @ViewById(R.id.rl_recommend)
    View rl_recommend;

    @ViewById(R.id.tv_recommend_status)
    TextView tv_recommend_status;

    //4.7 add 身份识别
    @ViewById(R.id.txt_activity_customer_detail_land)
    TextView tv_houseLandLordIdentity;//房东身份 1--是 0--否
    @ViewById(R.id.txt_activity_customer_detail_colleague)
    TextView tv_agentIdentity;//经纪人同事身份 1--是 0--否
    @ViewById(R.id.txt_activity_customer_detail_peer)
    TextView tv_intermediaryIdentity;//外部经纪人，同行身份 1--是 0--否

    ChatPresenter chatPresenter;

    private Dialog recommendSendDialog;//推荐发送对话框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatPresenter = new ChatPresenter(this);
        EventBus.getDefault().register(this);
        if (getIntent().getExtras() == null || getIntent().getExtras().getLong("mUserId") == 0) {
            mUserId = ChatActivity.currentUserId;
        }
    }


    @AfterViews
    public void init() {
        //引导页
        if(!PreferenceUtil.build().getBoolean(PreferenceUtil.PreferenceKEY.CUSTOMER_GUIDE)) {
            Intent intent = new Intent(this, GeneratedClassUtils.get(GuideActivity.class));
            intent.putExtra(GuideActivity.INTENT_CUSTOMER,true);
            startActivity(intent);
        }

        detailsController = new CustomerDetailsPresenter(this);
        title_txt.setText("客户详情");
        mtextbtn_add.setVisibility(View.VISIBLE);
        mtextbtn_add.setText("跟进");
        getDetailsRequest();
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                //解决IndexViewPager高度固定导致的屏幕初始位置不是顶部
                mScrollView.scrollTo(0, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatPresenter.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(ChatPresenter.OnMessageReceivedEvent onMessageReceivedEvent) {
        getDetailsRequest();
    }

    public void onEventMainThread(ChatPresenter.OnAuthorizeAcceptedEvent onAuthorizeAcceptedEvent) {
        refresh();
    }

    private void refresh() {
        chatPresenter.resume();
        refreshUnreadMessageCount();

        // 从其他页面返回都重新加载，以便呈现新的显示样式
        if (!isInitTab) {
            getDetailsRequest();
        }
    }

    @UiThread
    public void refreshUnreadMessageCount() {
        if (mCustomerDetailsResponse != null &&
                mCustomerDetailsResponse.getData()!=null &&
                !TextUtils.isEmpty(mCustomerDetailsResponse.getData().getHxUserId())) {

            EMConversation emConversation = EMClient.getInstance().chatManager().getConversation(mCustomerDetailsResponse.getData().getHxUserId());
            int count = 0;
            if (emConversation != null) {
                count = emConversation.getUnreadMsgCount();
            }
            if (count > 0) {
                tvUnreadMsgCount.setVisibility(View.VISIBLE);
                if (count >99) {
                    tvUnreadMsgCount.setText("99+");
                } else {
                    tvUnreadMsgCount.setText(String.valueOf(count));
                }
            } else {
                tvUnreadMsgCount.setVisibility(View.GONE);
            }

            if(mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C")){
                tvUnreadMsgCount.setBackgroundResource(R.drawable.unread_message_count_blacklist_bg);
            } else {
                tvUnreadMsgCount.setBackgroundResource(R.drawable.unread_message_count_bg);
            }
        }
    }

    /**
     * 设置在线聊天是否可点击
     * @param enable
     */
    private void setChatEnable(boolean enable) {
        if(mCustomerDetailsResponse.getData().getCustomer() != null && mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C")) {
            ifChat.setTextColor(getResources().getColor(R.color.color_999));
            llChatOnline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogBuilder.showSimpleDialog("客户已设置免打扰，您没有权限联系该客户", mActivity);
                }
            });
        }else {
            if (enable) {
                ifChat.setTextColor(getResources().getColor(R.color.color_607d8b));
                llChatOnline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chatOnline(mUserId);
                    }
                });
            } else {
                ifChat.setTextColor(getResources().getColor(R.color.color_999));
                llChatOnline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mActivity)
                                .setMessage(Html.fromHtml("请推荐用户安装最新版App并<font color = '#1abc9c'>登录使用 </font>"))
                                .setPositiveButton("关闭", null).show();
                    }
                });
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String from = intent.getStringExtra("from");

        if (getIntent().getExtras() == null || getIntent().getExtras().getLong("mUserId") == 0) {
            mUserId = ChatActivity.currentUserId;
        }

        if (HouseInfoActivity.class.getName().equals(from)) {
            mIndicator.onPageSelected(0);
        }

        if (this.getClass().getName().equals(from) || AgendaCreateActivity.class.getName().equals(from)
                || ScheduleDetailsActivity.class.getName().equals(from)) {
            mIndicator.onPageSelected(intent.getIntExtra(EXTRA_REQUEST_STATE, 0));
        }
    }

    private void updateBlackListDb() {

        boolean delete = false;

        String userId = null;
        String hxUserId = null;

        try {
            userId = String.valueOf(mCustomerDetailsResponse.getData().getCustomer().getId());
            hxUserId = mCustomerDetailsResponse.getData().getHxUserId();
            delete = !mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userId == null || hxUserId == null) {
            return;
        }


        if (!delete) {//拉黑
                DisableChatDBUtil.getInstance().insert(userId,hxUserId);
        } else { //解除拉黑
            DisableChatDBUtil.getInstance().delete(userId);
        }
    }


    /**
     * 初始化配盘列表Tab
     */
    public void initTabView() {
        View all = setTabBottomView(this, "全部");
        View will = setTabBottomView(this, "未看");
        View looked = setTabBottomView(this, "已看");
        View none = setTabBottomView(this, "不看了");
        views[0] = all;
        views[1] = will;
        views[2] = looked;
        views[3] = none;
        //默认选中小区Tab、设置字体颜色
        CheckBox tabCheckBox = (CheckBox) views[0].findViewById(R.id.attention_tab_box);
        tabCheckBox.setChecked(true);

        mViewPager.setCanScroll(true);
        mTabHost.setup();
        attentionPagerAdapter = new CustomerDetailsPagerAdapter(this, mTabHost, mViewPager,
                getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(4);

        Bundle bundleAll = new Bundle();
        bundleAll.putBoolean("mIsCustomDeal", isCustomDeal);
        bundleAll.putLong("mUserId", mUserId);
        bundleAll.putString("mCustomerName", mCustomerDetailsResponse.getData().getCustomer().getRealName());
        bundleAll.putBoolean("isStrongBindRank", detailsController.isStrongBindRank(mCustomerDetailsResponse.getData().getBindRank()));
        bundleAll.putBoolean("mhasPullBlack", mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C"));
        attentionPagerAdapter.addTab(mTabHost.newTabSpec(TAG_ALL).setIndicator
                (all), GeneratedClassUtils.getInstance(CustomerDetailsFragment.class)
                .getClass(), bundleAll);

        attentionPagerAdapter.addTab(mTabHost.newTabSpec(TAG_WILL).setIndicator
                (will), GeneratedClassUtils.getInstance(CustomerDetailsFragment.class)
                .getClass(), bundleAll);

        attentionPagerAdapter.addTab(mTabHost.newTabSpec(TAG_LOOKED).setIndicator
                (looked), GeneratedClassUtils.getInstance(CustomerDetailsFragment.class)
                .getClass(), bundleAll);

        attentionPagerAdapter.addTab(mTabHost.newTabSpec(TAG_NONE).setIndicator
                (none), GeneratedClassUtils.getInstance(CustomerDetailsFragment.class)
                .getClass(), bundleAll);


        mIndicator.setViewPager(mViewPager);
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mSelectedPage = position;
                attentionPagerAdapter.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                mSelectedPage = position;
                attentionPagerAdapter.onPageSelected(position);
                //选中的Tab标题颜色设置
                if (position < views.length) {
                    for (int i = 0; i < views.length; i++) {
                        CheckBox tabCheckBox = (CheckBox) views[i].findViewById(R.id.attention_tab_box);
                        tabCheckBox.setChecked(false);
                    }
                    CheckBox currentCheckBox = (CheckBox) views[position].findViewById(R.id.attention_tab_box);
                    currentCheckBox.setChecked(true);
                }
                CustomerDetailsFragment baseFragment = (CustomerDetailsFragment) attentionPagerAdapter.getItem(position);

                if (position == 0) {
                    baseFragment.reloadData(REQUEST_STATE_ALL, mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C"),detailsController.isStrongBindRank(mCustomerDetailsResponse.getData().getBindRank()));
                } else if (position == 1) {
                    baseFragment.reloadData(REQUEST_STATE_WILL, mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C"),detailsController.isStrongBindRank(mCustomerDetailsResponse.getData().getBindRank()));
                } else if (position == 2) {
                    baseFragment.reloadData(REQUEST_STATE_LOOKED, mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C"),detailsController.isStrongBindRank(mCustomerDetailsResponse.getData().getBindRank()));
                } else if (position == 3) {
                    baseFragment.reloadData(REQUEST_STATE_NO, mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C"),detailsController.isStrongBindRank(mCustomerDetailsResponse.getData().getBindRank()));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                attentionPagerAdapter.onPageScrollStateChanged(state);
            }
        });
        mScrollView.setmFragmentList(attentionPagerAdapter.getFragmentList());

    }
    public View setTabBottomView(Context mContext, String titleStr) {
        View tabTopview = View.inflate(mContext, R.layout.activity_customer_details_top_tab_layout, null);
        CheckBox tabCheckBox = (CheckBox) tabTopview.findViewById(R.id.attention_tab_box);
        tabCheckBox.setText(titleStr);
        return tabTopview;
    }
    /**
     * 手动配盘
     */
    @Click(R.id.distribute_house)
    public void gotoArrangement() {
        if (mCustomerDetailsResponse == null || mCustomerDetailsResponse.getData() == null){
            return;
        }

        if(mCustomerDetailsResponse.getData().getCustomer() != null && mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C")) {
            DialogBuilder.showSimpleDialog("客户已设置免打扰，您没有权限为该客户带看", mActivity);
        }else {
            Intent intent = new Intent(this, GeneratedClassUtils.get(HousesSourceActivity.class));
            intent.putExtra("userId", mCustomerDetailsResponse.getData().getCustomer().getId());
            intent.putExtra("currentTab", mCurrentTab);
            intent.putExtra("distributeSource", 1);
            intent.putExtra("userRequirementData",userRequirementData);
            intent.putExtra("from", CustomerDetailsActivity.class.getName());
            startActivityForResult(intent, REQUEST_ADD_DISTRIBUTE);
        }
    }

    @Click(R.id.iv_guide)
    public void showGuide() {
        Intent intent = new Intent(this, GeneratedClassUtils.get(GuideActivity.class));
        intent.putExtra(GuideActivity.INTENT_CUSTOMER,true);
        startActivity(intent);
    }

    /**
     * 更改客户类型
     */
    @Click(R.id.iv_custom_type)
    public void ChangeCustomType() {
        getUserLimit(mCustomerDetailsResponse.getData());
    }


    /**
     * 检查经纪人是否有权限
     * @param mUserInfo
     */
    private void getUserLimit(final CustomerDetailsData mUserInfo){
        showLoadingView();
        CheckUserLimitRequest mCheckUserLimitRequest = new CheckUserLimitRequest();
        mCheckUserLimitRequest.setAgentId(UserInfoUtil.getLocalLoginResponse(mActivity).data.getAgentId());
        UserReqsAction.getCheckUserLimit(mActivity, mCheckUserLimitRequest, new SaleAgentHttpListener<CheckUserLimitResponse>(mActivity) {
            @Override
            public void onBusinessSuccess(CheckUserLimitResponse mCheckUserLimitResponse, String url) {
                changeCustomType(mUserInfo, mCheckUserLimitResponse);
            }

            @Override
            public void onBusinessFail(CheckUserLimitResponse mCheckUserLimitResponse, String url) {
                super.onBusinessFail(mCheckUserLimitResponse, url);
            }

            @Override
            public void onError(Exception exception, String url) {
                super.onError(exception, url);
            }

            @Override
            public void onCallbackEnd() {
                showNormalView();
            }
        });
    }

    private String userClass;

    /**
     * 改变客户类型
     * @param mUserInfo
     * @param mCheckUserLimitResponse
     */
    private void changeCustomType(final CustomerDetailsData mUserInfo,final CheckUserLimitResponse mCheckUserLimitResponse){

        View contentView = LayoutInflater.from(mActivity).inflate(
                R.layout.dialogue_change_custom_type, null);
        final Dialog mDialog = DialogManager.showCenterAlert(mActivity, contentView);
        final TextView tv_1_hint = (TextView) contentView.findViewById(R.id.tv_1_hint);
        final TextView tv_2_hint = (TextView) contentView.findViewById(R.id.tv_2_hint);
        final TextView tv_3_hint = (TextView) contentView.findViewById(R.id.tv_3_hint);
        final TextView tv_1_shuoming = (TextView) contentView.findViewById(R.id.tv_1_shuoming);
        final TextView tv_2_shuoming = (TextView) contentView.findViewById(R.id.tv_2_shuoming);
        final TextView tv_3_shuoming = (TextView) contentView.findViewById(R.id.tv_3_shuoming);
        final ImageView iv_selected_1 = (ImageView) contentView.findViewById(R.id.iv_selected_1);
        final ImageView iv_selected_2 = (ImageView) contentView.findViewById(R.id.iv_selected_2);
        final ImageView iv_selected_3 = (ImageView) contentView.findViewById(R.id.iv_selected_3);
        final TextView tv_sure = (TextView) contentView.findViewById(R.id.tv_sure);
        final TextView tv_cancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        tv_1_hint.setText(mCheckUserLimitResponse.getUserLimitA().getMessage());
        tv_2_hint.setText(mCheckUserLimitResponse.getUserLimitB().getMessage());
        tv_3_hint.setText(mCheckUserLimitResponse.getUserLimitD().getMessage());
        LinearLayout ll_1 = (LinearLayout) contentView.findViewById(R.id.ll_1);

        if(null == mCheckUserLimitResponse.getUserLimitA().getTip())
        {
            tv_1_shuoming.setVisibility(View.GONE);
        }
        else
        {
            tv_1_shuoming.setText(mCheckUserLimitResponse.getUserLimitA().getTip());
        }

        if(null == mCheckUserLimitResponse.getUserLimitB().getTip())
        {
            tv_2_shuoming.setVisibility(View.GONE);
        }
        else
        {
            tv_2_shuoming.setText(mCheckUserLimitResponse.getUserLimitB().getTip());
        }


        userClass = mUserInfo.getCustomer().getUserClass();
        if(userClass.equals("A")){
            iv_selected_1.setImageResource(R.drawable.checkbox_checked);
            iv_selected_2.setImageResource(R.drawable.checkbox_uncheck);
            iv_selected_3.setImageResource(R.drawable.checkbox_uncheck);
            tv_2_hint.setVisibility(View.GONE);
            tv_3_hint.setVisibility(View.GONE);
            if(mCheckUserLimitResponse.getUserLimitA().getIsUpLimit())
            {
                tv_1_hint.setVisibility(View.VISIBLE);
                tv_sure.setEnabled(false);
                tv_sure.setTextColor(getResources().getColor(R.color.bg_select_disabled));
            }
            else {
                tv_sure.setEnabled(true);
                tv_sure.setTextColor(getResources().getColor(R.color.color_333333));
            }
        }else if(userClass.equals("B")){
            userClass = "B";
            iv_selected_1.setImageResource(R.drawable.checkbox_uncheck);
            iv_selected_2.setImageResource(R.drawable.checkbox_checked);
            iv_selected_3.setImageResource(R.drawable.checkbox_uncheck);
            tv_1_hint.setVisibility(View.GONE);
            tv_3_hint.setVisibility(View.GONE);
            if(mCheckUserLimitResponse.getUserLimitB().getIsUpLimit())
            {
                tv_2_hint.setVisibility(View.VISIBLE);
                tv_sure.setEnabled(false);
                tv_sure.setTextColor(getResources().getColor(R.color.bg_select_disabled));
            }
            else {
                tv_sure.setEnabled(true);
                tv_sure.setTextColor(getResources().getColor(R.color.color_333333));
            }
        }else if(userClass.equals("D")){
            userClass = "D";
            iv_selected_1.setImageResource(R.drawable.checkbox_uncheck);
            iv_selected_2.setImageResource(R.drawable.checkbox_uncheck);
            iv_selected_3.setImageResource(R.drawable.checkbox_checked);
            tv_1_hint.setVisibility(View.GONE);
            tv_2_hint.setVisibility(View.GONE);
            tv_sure.setEnabled(true);
            tv_sure.setTextColor(mActivity.getResources().getColor(R.color.color_333333));
            tv_3_hint.setVisibility(View.VISIBLE);
        }
        ll_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                userClass = "A";
                iv_selected_1.setImageResource(R.drawable.checkbox_checked);
                iv_selected_2.setImageResource(R.drawable.checkbox_uncheck);
                iv_selected_3.setImageResource(R.drawable.checkbox_uncheck);
                tv_2_hint.setVisibility(View.GONE);
                tv_3_hint.setVisibility(View.GONE);
                if(mCheckUserLimitResponse.getUserLimitA().getIsUpLimit())
                {
                    tv_1_hint.setVisibility(View.VISIBLE);
                    tv_sure.setEnabled(false);
                    tv_sure.setTextColor(getResources().getColor(R.color.bg_select_disabled));
                }
                else {
                    tv_sure.setEnabled(true);
                    tv_sure.setTextColor(getResources().getColor(R.color.color_333333));
                }
            }
        });
        LinearLayout ll_2 = (LinearLayout) contentView.findViewById(R.id.ll_2);
        ll_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                userClass = "B";
                iv_selected_1.setImageResource(R.drawable.checkbox_uncheck);
                iv_selected_2.setImageResource(R.drawable.checkbox_checked);
                iv_selected_3.setImageResource(R.drawable.checkbox_uncheck);
                tv_1_hint.setVisibility(View.GONE);
                tv_3_hint.setVisibility(View.GONE);
                if(mCheckUserLimitResponse.getUserLimitB().getIsUpLimit())
                {
                    tv_2_hint.setVisibility(View.VISIBLE);
                    tv_sure.setEnabled(false);
                    tv_sure.setTextColor(getResources().getColor(R.color.bg_select_disabled));
                }
                else {
                    tv_sure.setEnabled(true);
                    tv_sure.setTextColor(getResources().getColor(R.color.color_333333));
                }
            }
        });
        LinearLayout ll_3 = (LinearLayout) contentView.findViewById(R.id.ll_3);
        ll_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                userClass = "D";
                iv_selected_1.setImageResource(R.drawable.checkbox_uncheck);
                iv_selected_2.setImageResource(R.drawable.checkbox_uncheck);
                iv_selected_3.setImageResource(R.drawable.checkbox_checked);
                tv_1_hint.setVisibility(View.GONE);
                tv_2_hint.setVisibility(View.GONE);
                tv_sure.setEnabled(true);
                tv_sure.setTextColor(mActivity.getResources().getColor(R.color.color_333333));
                tv_3_hint.setVisibility(View.VISIBLE);
            }
        });

        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                EditUserClassRequest mEditUserClassRequest = new EditUserClassRequest();
                mEditUserClassRequest.setAgentId(UserInfoUtil.getLocalLoginResponse(mActivity).data.getAgentId());
                mEditUserClassRequest.setUserId(mUserInfo.getCustomer().getId());
                mEditUserClassRequest.setUserClass(userClass);
                mEditUserClassRequest.setPreUserClass(mUserInfo.getCustomer().getUserClass());
                changeUserType(mEditUserClassRequest);
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    /**
     * 更改客户类型请求
     * @param mEditUserClassRequest
     */
    private void changeUserType(final EditUserClassRequest mEditUserClassRequest){
        showLoadingView();
        UserReqsAction.getEditUserClass(mActivity, mEditUserClassRequest, new SaleAgentHttpListener<EditUserClassResponse>(mActivity) {
            @Override
            public void onBusinessSuccess(EditUserClassResponse mEditUserClassResponse, String url) {
                mCustomerDetailsResponse.getData().getCustomer().setUserClass(mEditUserClassRequest.getUserClass());
                if (Constants.CUSTOMER_CLASS_A.equals(mEditUserClassRequest.getUserClass())) {
                    mIVCustomerType.setImageResource(R.drawable.customer_good_detail);
                } else if (Constants.CUSTOMER_CLASS_B.equals(mEditUserClassRequest.getUserClass())) {
                    mIVCustomerType.setImageResource(R.drawable.customer_normal_detail);
                } else {
                    mIVCustomerType.setVisibility(View.GONE);
                    Intent intent = new Intent(mActivity, GeneratedClassUtils.get(MainActivity.class));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mActivity.startActivity(intent);
                    mActivity.finish();
                }
            }

            @Override
            public void onBusinessFail(EditUserClassResponse mEditUserClassResponse, String url) {
                super.onBusinessFail(mEditUserClassResponse, url);
            }

            @Override
            public void onError(Exception exception, String url) {
                super.onError(exception, url);
            }

            @Override
            public void onCallbackEnd() {
                showNormalView();
            }
        });
    }

    /**
     * 联系客户. 如果不在密号体系内，zhijieyong号码
     * 如果在密号体系内，如果没密号，申请密号再用密号联系；有密号用密号联系。
     * @param actionType 1 表示电话 2表示短信
     */
    private static final int CALL = 1;
    private static final int MMS = 2;
    private void contactClient(final int actionType) {
        if (mCustomerDetailsResponse != null && mCustomerDetailsResponse.getData() != null
                && mCustomerDetailsResponse.getData().getCustomer() != null
                && mCustomerDetailsResponse.getData().getCustomer().getMobile() != null) {
            if (!mCustomerDetailsResponse.getData().isSecretMark()) {//不在密号体系内
                if (CALL == actionType) {
                    gotoCallScreen(mCustomerDetailsResponse.getData().getCustomer().getMobile());
                } else {
                    if (!PhoneNoUtil.isMobile(mCustomerDetailsResponse.getData().getCustomer().getMobile())) {
                        DialogBuilder.showSimpleDialog(getString(R.string.not_phone_number_info), mActivity);
                        return;
                    }
                    gotoMmsScreen(mCustomerDetailsResponse.getData().getCustomer().getMobile());
                }
            } else {//在密号体系内
                if (TextUtils.isEmpty(mCustomerDetailsResponse.getData().getSecretPhone())) { //没有密号，申请分配密号
                    showLoadingView();
                    UserReqsAction.getSecretPhone(this, AppConfig.agentid.toString(), mUserId, new SaleAgentHttpListener<UserSecretPhoneResponse>(this) {
                        @Override
                        public void onBusinessSuccess(UserSecretPhoneResponse userSecretPhoneResponse, String url) {
                            if (null != userSecretPhoneResponse && null != userSecretPhoneResponse.getData()
                                    && !TextUtils.isEmpty(userSecretPhoneResponse.getData().getSecretPhone())) {
                                mCustomerDetailsResponse.getData().setSecretPhone(userSecretPhoneResponse.getData().getSecretPhone());
                                if (CALL == actionType) {
                                    gotoCallScreen(mCustomerDetailsResponse.getData().getSecretPhone());
                                } else {
                                    if (!PhoneNoUtil.isMobile(mCustomerDetailsResponse.getData().getCustomer().getMobile())) {
                                        DialogBuilder.showSimpleDialog(getString(R.string.not_phone_number_info), mActivity);
                                        return;
                                    }
                                    gotoMmsScreen(mCustomerDetailsResponse.getData().getSecretPhone());
                                }
                            } else {
                                ToastUtil.show(CustomerDetailsActivity.this, "无法获取号码!");
                            }
                        }
                    });
                } else {//有密号，直接使用返回的密号
                    if (CALL == actionType) {
                        gotoCallScreen(mCustomerDetailsResponse.getData().getSecretPhone());
                    } else {
                        if (!PhoneNoUtil.isMobile(mCustomerDetailsResponse.getData().getCustomer().getMobile())) {
                            DialogBuilder.showSimpleDialog(getString(R.string.not_phone_number_info), mActivity);
                            return;
                        }
                        gotoMmsScreen(mCustomerDetailsResponse.getData().getSecretPhone());
                    }
                }
            }
        }
    }

    /**
     * 请确保该phone是正确的
     * @param phone
     */
    private void gotoCallScreen(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void gotoMmsScreen(String phone) {
        Uri smsToUri = Uri.parse("smsto:" + phone);
        Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        startActivity(mIntent);
    }

    /**
     * 打电话
     */
    public void callPhoneLayout() {

        if (CheckDoubleClick.isFastDoubleClick()) {
            return;
        }
        contactClient(CALL);
    }

    public void callPhoneIcon() {

        if (CheckDoubleClick.isFastDoubleClick()) {
            return;
        }
        contactClient(CALL);
    }

    /**
     * 发送短信事件
     */
    public void sendSmsTop() {
        if (CheckDoubleClick.isFastDoubleClick()) {
            return;
        }
        contactClient(MMS);
    }



    /**
     * 编辑客户信息
     */
    @Click(R.id.ll_edit)
    public void editClient() {
        if (CheckDoubleClick.isFastDoubleClick()) {
            return;
        }
        Bundle mBundle = new Bundle();
        if (mUserId != null) {
            mBundle.putLong("mUserId", mUserId);
        }
        if (mCustomerDetailsResponse != null && mCustomerDetailsResponse.getData() != null) {
            if(mCustomerDetailsResponse.getData().getCustomer()!=null){
                if (mCustomerDetailsResponse.getData().getCustomer().getMobile() != null) {
                    if (mCustomerDetailsResponse.getData().isSecretMark()) {
                        mBundle.putString("mPhoneNumber", getSecretPhoneNumber(mCustomerDetailsResponse.getData().getCustomer().getMobile()));
                    }else {
                        mBundle.putString("mPhoneNumber", mCustomerDetailsResponse.getData().getCustomer().getMobile());
                    }
                }
                if (mCustomerDetailsResponse.getData().getCustomer().getSource() != null) {
                    mBundle.putInt("mResource", mCustomerDetailsResponse.getData().getCustomer().getSource());
                }
                if (mCustomerDetailsResponse.getData().getCustomer().getGender() != null) {
                    mBundle.putInt("mGender", mCustomerDetailsResponse.getData().getCustomer().getGender());
                }
                if (mCustomerDetailsResponse.getData().getCustomer().getAddSource() != null) {
                    mBundle.putInt("mAddSource", mCustomerDetailsResponse.getData().getCustomer().getAddSource());
                }
                mBundle.putString("mName", mCustomerDetailsResponse.getData().getCustomer().getRealName());
                mBundle.putString("mMemo", mCustomerDetailsResponse.getData().getCustomer().getMemo());
            }
            mBundle.putBoolean("isStrongBindRank", detailsController.isStrongBindRank(mCustomerDetailsResponse.getData().getBindRank()));
            mBundle.putSerializable("userRequirementData",userRequirementData);
            Intent intent = new Intent(this, GeneratedClassUtils.get(EditCustomerActivity.class));
            intent.putExtras(mBundle);
            startActivityForResult(intent, REQUEST_ACTION_EDIT_CUSTOMER);
        }
    }

    /**
     * 跟进点击事件
     * 展示全部跟进且可新增跟进
     */
    @Click(R.id.textbtn_add)
    public void searchFollow() {
        Intent intent = new Intent(this, GeneratedClassUtils.get(CustomerFollowUpListActivity.class));
        Bundle bundle = new Bundle();
        if (mUserId != null) {
            bundle.putLong("mUserId", mUserId);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * 返回
     */
    @Click(R.id.btn_back)
    public void onBack() {


        if (CheckDoubleClick.isFastDoubleClick()) {
            return;
        }
        remove();
    }

    /**
     * 获取客户详情request
     */
    public void getDetailsRequest() {
        CustomerDetailsRequest mCustomerDetailsRequest = new CustomerDetailsRequest();
        mCustomerDetailsRequest.setUserId(mUserId);
        mCustomerDetailsRequest.setAgentId(AppConfig.agentid);
        showLoadingView();
        UserReqsAction.getDetails(mActivity, mCustomerDetailsRequest, new SaleAgentHttpListener<CustomerDetailsResponse>(this) {
            @Override
            public void onBusinessSuccess(CustomerDetailsResponse response, String url) {
                mCustomerDetailsResponse = response;

                updateBlackListDb();

                userRequirementData = response.getData().getRequirement();
                //TODO 假数据
//                mCustomerDetailsResponse.getData().setHasPullBlack(1);
                setCustomerInfos();
                refreshUnreadMessageCount();
                requestCustomDemand();
                if (isInitTab) {
                    initTabView();
                    isInitTab = false;
                } else {
                    mIndicator.onPageSelected(mSelectedPage); // 会去reload配盘清单
                }
            }
        });
    }

    /**
     * 获取用户需求详情
     */
    private void requestCustomDemand() {
//        UserRequirementRequest userRequirementRequest = new UserRequirementRequest();
//        userRequirementRequest.setAgentId(mUserId);
//        UserReqsAction.getUserRequirement(this, userRequirementRequest, new SaleAgentHttpListener<UserRequirementResponse>(this) {
//            @Override
//            public void onBusinessSuccess(UserRequirementResponse userRequirementResponse, String url) {
//
//                if (userRequirementResponse != null) {
//                    if (userRequirementData == null || userRequirementResponse.data == null)
//                        return;
//                    userRequirementData.setTotal(userRequirementResponse.data.getTotal());
//                }
//
//            }
//        });
    }
    /**
     * 如果是强关系 用户类型沿用以前的逻辑
     */
    private void setCustomerInfosByRank(boolean enable) {
        if (enable) {//强关系
            //不在密号体系内。不显示密号；在密号体系内，如果有密号，显示密号。
            if (!mCustomerDetailsResponse.getData().isSecretMark()){
                mClientPhone.setText(mCustomerDetailsResponse.getData().getCustomer().getMobile());
                mClientPhone.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.step_dp_16));
                mClientPhone.setTextColor(getResources().getColor(R.color.color_333));

                mClientHiddenPhone.setVisibility(View.GONE);
            } else {
                mClientPhone.setText(getSecretPhoneNumber(mCustomerDetailsResponse.getData().getCustomer().getMobile()) + "(原号)");
                mClientPhone.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.step_dp_16));
                mClientPhone.setTextColor(getResources().getColor(R.color.color_999));

                if (!TextUtils.isEmpty(mCustomerDetailsResponse.getData().getSecretPhone())) {
                    mClientPhone.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.step_dp_10));
                    mClientHiddenPhone.setVisibility(View.VISIBLE);
                    mClientHiddenPhone.setText(mCustomerDetailsResponse.getData().getSecretPhone() + "(密号)");
                } else {
                    mClientHiddenPhone.setText("");
                    mClientHiddenPhone.setVisibility(View.GONE);
                }
            }

            //客户类型
            if(mCustomerDetailsResponse.getData().getCustomer() != null && mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C")) {//拉黑
                mIVCustomerType.setClickable(false);
                mIVCustomerType.setImageResource(R.drawable.customer_disturb);
                ifChat.setTextColor(getResources().getColor(R.color.color_999));
                ifSMS.setTextColor(getResources().getColor(R.color.color_999));
                ifCall.setTextColor(getResources().getColor(R.color.color_999));
                mClientPhone.setText(getSecretPhoneNumber(mCustomerDetailsResponse.getData().getCustomer().getMobile()));
                mClientPhone.setTextColor(getResources().getColor(R.color.color_999));
                mClientPhone.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.step_dp_16));
                mClientHiddenPhone.setVisibility(View.GONE);
            }else {
                if (isCustomDeal) {//签单客户
                    mIVCustomerType.setClickable(false);
                    mIVCustomerType.setImageResource(R.drawable.customer_qiandan_detail);
                    mIVCustomerType.setVisibility(View.GONE);
                } else {

                    mIVCustomerType.setVisibility(View.VISIBLE);
                    mIVCustomerType.setClickable(true);
                    if (Constants.CUSTOMER_CLASS_A.equals(mCustomerDetailsResponse.getData().getCustomer().getUserClass())) {
                        mIVCustomerType.setImageResource(R.drawable.customer_good_detail);
                    } else if (Constants.CUSTOMER_CLASS_B.equals(mCustomerDetailsResponse.getData().getCustomer().getUserClass())) {
                        mIVCustomerType.setImageResource(R.drawable.customer_normal_detail);
                    } else {
                        mIVCustomerType.setVisibility(View.GONE);
                    }
                }
            }
            llCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mCustomerDetailsResponse.getData().getCustomer() != null && mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C")) {

                        DialogBuilder.showSimpleDialog("客户已设置免打扰，您没有权限联系该客户", mActivity);
                    }else {
                        callPhoneLayout();
                    }
                }
            });

            llSendSMS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mCustomerDetailsResponse.getData().getCustomer() != null && mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C")) {

                        DialogBuilder.showSimpleDialog("客户已设置免打扰，您没有权限联系该客户", mActivity);
                    }else {
                        sendSmsTop();
                    }
                }
            });

        } else {//弱关系
            if(mCustomerDetailsResponse.getData().getCustomer() != null && mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C")) {//拉黑
                mIVCustomerType.setClickable(false);
                mIVCustomerType.setImageResource(R.drawable.customer_disturb);
            }else {
                mIVCustomerType.setClickable(false);
                mIVCustomerType.setImageResource(R.drawable.ic_chat_default);
            }
            //弱关系不显示号码 且图标要置灰
            llCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckDoubleClick.isFastDoubleClick()) return;

                    if(mCustomerDetailsResponse.getData().getCustomer() != null && mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C")) {
                        DialogBuilder.showSimpleDialog("客户已设置免打扰，您没有权限联系该客户", mActivity);
                    }else {
                        // 向客户请求授权
                        showAuthorizeDialog();
                    }
                }
            });
            llSendSMS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckDoubleClick.isFastDoubleClick()) return;

                    if(mCustomerDetailsResponse.getData().getCustomer() != null && mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C")) {
                        DialogBuilder.showSimpleDialog("客户已设置免打扰，您没有权限联系该客户", mActivity);
                    }else {
                        // 向客户请求授权
                        showAuthorizeDialog();
                    }
                }
            });

            ifCall.setTextColor(getResources().getColor(R.color.color_999));

            ifSMS.setTextColor(getResources().getColor(R.color.color_999));

            mClientPhone.setTextColor(getResources().getColor(R.color.color_999));
            mClientPhone.setVisibility(View.VISIBLE);
            mClientPhone.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.step_dp_16));
            if (mCustomerDetailsResponse!=null &&
                    mCustomerDetailsResponse.getData() !=null
                    && mCustomerDetailsResponse.getData().getCustomer() !=null
                    && !TextUtils.isEmpty(mCustomerDetailsResponse.getData().getCustomer().getMobile())) {
                mClientPhone.setText(getSecretPhoneNumber(mCustomerDetailsResponse.getData().getCustomer().getMobile()));
            }

            mClientHiddenPhone.setVisibility(View.GONE);


        }
    }

    /**
     * 向客户请求授权
     */
    public void showAuthorizeDialog() {
        // 不在密号体系
        if (mCustomerDetailsResponse != null && mCustomerDetailsResponse.getData() != null && !mCustomerDetailsResponse.getData().isSecretMark()) {
            DialogBuilder.showSimpleDialog("当前客户为微聊客户，不允许电话联系或带看", this);
            return;
        }

        View view = LayoutInflater.from(mTopActivity).inflate(R.layout.dialog_authorize_prompt, null);

        final AlertDialog alertPrompt = new AlertDialog.Builder(mTopActivity).setView(view).create();
        alertPrompt.setCanceledOnTouchOutside(false);
        alertPrompt.setCancelable(false);
        alertPrompt.show();
        alertPrompt.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvSubmit = (TextView) view.findViewById(R.id.tv_submit);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_content);

        tvContent.setText(Html.fromHtml("向客户请求授权，成为你的<font color=\"#333333\" size=16><strong>找房中客户</strong></font>。客户确认后，你就可以通过电话联系和带看了"));

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckDoubleClick.isFastDoubleClick()) return;
                if (alertPrompt != null && alertPrompt.isShowing()) {
                    alertPrompt.dismiss();
                }

                GetUserAuthorizeRequest request = new GetUserAuthorizeRequest();
                AgentToUserMessageExt ext = new AgentToUserMessageExt();
                ext.setType(2);
                ext.setMsgTitle("对方申请使用转接号联系您，点击查看详情。");
                ext.setMsgContent("电话联系申请");

                request.setUserId(mUserId);
                request.setAgentId(AppConfig.agentid);
                request.setExt(ext);

                UserReqsAction.getUserAuthorize(mTopActivity, request, new SaleAgentHttpListener<GetUserAuthorizeResponse>(mTopActivity) {
                    @Override
                    public void onBusinessSuccess(GetUserAuthorizeResponse getUserAuthorizeResponse, String url) {
                        ToastUtil.show(mTopActivity, "授权请求已发送成功");
                        CustomerDetailsData.Customer customer = mCustomerDetailsResponse.getData().getCustomer();
                        ChatUtil.insertSystemText("授权请求已发送成功", mCustomerDetailsResponse.getData().getHxUserId(),customer.getRealName(),customer.getMemo(),customer.getId()); // 将提示内容存入数据库可显示于微聊界面中
                        chatOnline(mUserId); // 跳转到微聊界面
                    }

                    // errorCode -1:客戶APP版本過低, -2:一天一次请求 -3:不在密号体系内(北京, 上海, 南京, 天津)
                    @Override
                    public void onBusinessFail(GetUserAuthorizeResponse getUserAuthorizeResponse, String url) {
                        if (getUserAuthorizeResponse != null) {
                            if (getUserAuthorizeResponse.getErrorCode() == -1) {
                                SendAuthorizeHelper.showVersionNotSupportedDialog(mActivity, getUserAuthorizeResponse.getMessage(), new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        chatOnline(mUserId);
                                    }
                                });
                            }
                            else {
                                super.onBusinessFail(getUserAuthorizeResponse, url);
                            }
                        }
                    }
                });
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckDoubleClick.isFastDoubleClick()) return;
                if (alertPrompt != null && alertPrompt.isShowing()) {
                    alertPrompt.dismiss();
                }
            }
        });
    }

    /**
     * 设置客户详情信息
     */
    @UiThread
    public void setCustomerInfos() {
        if (mCustomerDetailsResponse != null) {
            if(mCustomerDetailsResponse.getData() == null){
                ToastUtil.show(mActivity,"用户数据错误!");
                return;
            }
            if (mCustomerDetailsResponse.getData().getCustomer() == null) {
                ToastUtil.show(mActivity,"用户信息错误!");
                return;
            }
            //设置客户ID
            title_txt2.setText("客户ID："+Long.toString(mCustomerDetailsResponse.getData().getCustomer().getId()));
            //设置姓名性别
            if (mCustomerDetailsResponse.getData().getCustomer().getGender() == 1) {
                mClientName.setText(getString(R.string.sex_man, mCustomerDetailsResponse.getData().getCustomer().getRealName()));
            } else if (mCustomerDetailsResponse.getData().getCustomer().getGender() == 2) {
                mClientName.setText(getString(R.string.sex_woman, mCustomerDetailsResponse.getData().getCustomer().getRealName()));
            } else if (mCustomerDetailsResponse.getData().getCustomer().getGender() == 3) {
                mClientName.setText(mCustomerDetailsResponse.getData().getCustomer().getRealName());
            }

            setCustomerInfosByRank(detailsController.isStrongBindRank(mCustomerDetailsResponse.getData().getBindRank()));
            //设置首次意向
            if (mCustomerDetailsResponse.getData().getCustomer().getAddSource()!= null) {
                txtFirstIntention.setVisibility(View.VISIBLE);
                String txt = "首次意向：";
                if (mCustomerDetailsResponse.getData().getCustomer().getAddSource() == AddCustomerActivity.FIRST_INTENTION_TYPE_SECOND_HAND_HOUSE){
                    txtFirstIntention.setText(txt+AddCustomerActivity.FIRST_INTENTION_TXT_SECOND_HAND_HOUSE);
                }else if(mCustomerDetailsResponse.getData().getCustomer().getAddSource() == AddCustomerActivity.FIRST_INTENTION_TYPE_NEW_HOUSE){
                    txtFirstIntention.setText(txt+AddCustomerActivity.FIRST_INTENTION_TXT_NEW_HOUSE);
                }else{
                    txtFirstIntention.setText(txt);
                }
            } else {
                txtFirstIntention.setVisibility(View.GONE);
            }
            //设置备注信息
            if (mCustomerDetailsResponse.getData().getCustomer().getMemo() != null) {
                ll_remarks.setVisibility(View.VISIBLE);
                client_remarks.setText(mCustomerDetailsResponse.getData().getCustomer().getMemo());
            } else {
                ll_remarks.setVisibility(View.GONE);
            }


            //设置APP用户名
            if (!StringUtil.isEmptyOrNull(mCustomerDetailsResponse.getData().getCustomer().getUserAppRealName())) {
                ll_appname.setVisibility(View.VISIBLE);
                tv_app_name.setText(mCustomerDetailsResponse.getData().getCustomer().getUserAppRealName());
            } else {
                ll_appname.setVisibility(View.GONE);
            }
            //客户需求【户型/面积/价格/楼层】

            if(mCustomerDetailsResponse.getData().getRequirement()!=null) {
                String des = "";
                UserRequirementDataWithId dec = mCustomerDetailsResponse.getData().getRequirement();
                if(!StringUtil.isEmptyOrNull(dec.getBedroomSum())){
                    des = dec.getBedroomSum()+"室";
                }
                if(dec.getSpaceAreaType() != null) {
                    if(dec.getSpaceAreaType()==1) {
                        des = des + "/" + "50㎡以下";
                    }else if(dec.getSpaceAreaType()==2) {
                        des = des + "/" + "50-70㎡";
                    }else if(dec.getSpaceAreaType()==3) {
                        des = des + "/" + "70-90㎡";
                    }else if(dec.getSpaceAreaType()==4) {
                        des = des + "/" + "90-110㎡";
                    }else if(dec.getSpaceAreaType()==5) {
                        des = des + "/" + "110-130㎡";
                    }else if(dec.getSpaceAreaType()==6) {
                        des = des + "/" + "130-150㎡";
                    }else if(dec.getSpaceAreaType()==7) {
                        des = des + "/" + "150-200㎡";
                    }else if(dec.getSpaceAreaType()==8) {
                        des = des + "/" + "200㎡以上";
                    }
                }

                if(dec.getPriceLow()!= null) {
                    des = des + "/" + dec.getPriceLow() + "万";
                }
                if(!StringUtil.isEmptyOrNull(dec.getFloorType()) && dec.getFloorType().length()>0) {
                    des = des + "/" ;
                    if(dec.getFloorType().contains(Constants.Floor_Type_one_floor)){
                        des = des+"一层";
                    }
                    if(dec.getFloorType().contains(Constants.Floor_Type_low_floor)) {
                        des = des + "," +"低层";
                    }
                    if(dec.getFloorType().contains(Constants.Floor_Type_middle_floor)) {
                        des = des + "," +"中层";
                    }
                    if(dec.getFloorType().contains(Constants.Floor_Type_high_floor)) {
                        des = des + "," +"高层";
                    }
                    if(dec.getFloorType().contains(Constants.Floor_Type_top_floor)) {
                        des = des + "," +"顶层";
                    }
                }

                mClientDemand.setText(des);
            }else {

                mClientDemand.setVisibility(View.GONE);
            }

            boolean enable = mCustomerDetailsResponse.getData().getIsHxVersion() == null?false
                    :mCustomerDetailsResponse.getData().getIsHxVersion()>0;
            setChatEnable(enable);

            //标签
            List<String> tags = mCustomerDetailsResponse.getData().getRemindTags();
            int[] tagsId = new int[]{R.id.tv_label_1, R.id.tv_label_2, R.id.tv_label_3, R.id.tv_label_4};
            int i = -1;
            for (String tag : tags) {
                i++;
                TextView tv = (TextView)findViewById(tagsId[i]);
                tv.setVisibility(View.VISIBLE);
                tv.setText(tag);
                if (tag.contains("回收")) {
                    tv.setTextColor(getResources().getColor(R.color.color_e74c3c));
                    tv.setBackgroundResource(R.drawable.bg_custom_label_e74c3c);
                }else {
                    tv.setTextColor(getResources().getColor(R.color.color_607d8b));
                    tv.setBackgroundResource(R.drawable.bg_custom_label_607d8b);
                }
            }
            i++;
            for (int pos = i; pos < 4; pos++) {
                findViewById(tagsId[pos]).setVisibility(View.GONE);
            }

        }

        rl_recommend.setVisibility(View.GONE);
        if(mCustomerDetailsResponse.getData().getCustomer().getHasApp() != 1) {//未安装app
            rl_recommend.setVisibility(View.VISIBLE);
            int introApp = mCustomerDetailsResponse.getData().getIntroApp();
            tv_recommend_status.setText("未推荐，未安装 >");
            if (introApp == 1) {//1. 已推荐，未安装
                tv_recommend_status.setText("已推荐，未安装 >");
            }
        }
        if(mCustomerDetailsResponse.getData().getCustomer() != null && mCustomerDetailsResponse.getData().getCustomer().getUserClass().equals("C")) {
            distribute_house.setBackgroundColor(getResources().getColor(R.color.color_999));
        } else {
            distribute_house.setBackgroundResource(R.drawable.bg_btn_selector);
        }

        //4.7 Add
        int houseLandLordIdentity = mCustomerDetailsResponse.getData().getHouseLandLordIdentity();
        int agentIdentity = mCustomerDetailsResponse.getData().getAgentIdentity();
        int intermediaryIdentity = mCustomerDetailsResponse.getData().getIntermediaryIdentity();

        int userIdentity = UserIdentityHelper.initUserIdentity(houseLandLordIdentity,agentIdentity,intermediaryIdentity);

        if(UserIdentityHelper.hasHouseLandLordIdentity(userIdentity)){
            tv_houseLandLordIdentity.setVisibility(View.VISIBLE);
        }else{
            tv_houseLandLordIdentity.setVisibility(View.GONE);
        }
        if(UserIdentityHelper.hasAgentIdentity(userIdentity)){
            tv_agentIdentity.setVisibility(View.VISIBLE);
        }else{
            tv_agentIdentity.setVisibility(View.GONE);
        }
        if(UserIdentityHelper.hasIntermediaryIdentity(userIdentity)){
            tv_intermediaryIdentity.setVisibility(View.VISIBLE);
        }else{
            tv_intermediaryIdentity.setVisibility(View.GONE);
        }
    }

    private String getSecretPhoneNumber(String originalPhoneNum) {
        String phone = originalPhoneNum;
        //防止服务器传过来的号码有问题。
        if (null != phone && phone.length() > 4) {
            phone = phone.substring(0, 3) + "xxxx" + phone.substring(phone.length() - 4);
        }
        return phone;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_ACTION_EDIT_CUSTOMER:
//            case REQUEST_ACTION_ADD_MEMO:
//                isInitTab = false;
//                getDetailsRequest();
//                break;
//        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_ADD_DISTRIBUTE) {
            mIndicator.onPageSelected(0);
        }
    }

    /**
     * 从服务器获取环信用户名昵称,然后发起聊天
     * @param userId
     */
    private void chatOnline(final long userId) {

        if (TextUtils.isEmpty(mCustomerDetailsResponse.getData().getHxUserId())) { //如果环信id为空 请求接口
            requestForHXInfo(userId);
            return;
        }

        WeakHxUserInfo weakHxUserInfo = new WeakHxUserInfo(mCustomerDetailsResponse.getData().getHxUserId());
        weakHxUserInfo.setBindRank(mCustomerDetailsResponse.getData().getBindRank());
        weakHxUserInfo.setRealName(mCustomerDetailsResponse.getData().getCustomer().getUserAppRealName());
        weakHxUserInfo.setMemoName(mCustomerDetailsResponse.getData().getCustomer().getRealName());
        weakHxUserInfo.setUserId(userId);
        weakHxUserInfo.setBindRank(mCustomerDetailsResponse.getData().getBindRank());
        weakHxUserInfo.setMobile(mCustomerDetailsResponse.getData().getCustomer().getMobile());
        weakHxUserInfo.setSecretMark(mCustomerDetailsResponse.getData().isSecretMark());
        weakHxUserInfo.setSecretPhone(mCustomerDetailsResponse.getData().getSecretPhone());
        ChatActivity.launchChatActivity(mActivity,weakHxUserInfo);
    }

    private void requestForHXInfo(final long userId) {
        GetHXUserInfoRequest request = new GetHXUserInfoRequest();
        request.setIwId(userId);
        request.setIwType("2");
        showLoadingView();
        UserReqsAction.getHXUserInfo(mActivity, request, new SaleAgentHttpListener<HXUserResponse>(mActivity) {
            @Override
            public void onBusinessSuccess(HXUserResponse hxUserResponse, String url) {
                WeakHxUserInfo weakHxUserInfo = new WeakHxUserInfo(hxUserResponse.getData().getHximUserId());
                weakHxUserInfo.setBindRank(mCustomerDetailsResponse.getData().getBindRank());
                weakHxUserInfo.setRealName(mCustomerDetailsResponse.getData().getCustomer().getRealName());
                weakHxUserInfo.setUserId(userId);
                weakHxUserInfo.setBindRank(mCustomerDetailsResponse.getData().getBindRank());
                weakHxUserInfo.setMobile(mCustomerDetailsResponse.getData().getCustomer().getMobile());
                weakHxUserInfo.setSecretMark(mCustomerDetailsResponse.getData().isSecretMark());
                weakHxUserInfo.setSecretPhone(mCustomerDetailsResponse.getData().getSecretPhone());
                ChatActivity.launchChatActivity(mActivity,weakHxUserInfo);
            }

            @Override
            public void onBusinessFail(HXUserResponse hxUserResponse, String url) {
                super.onBusinessFail(hxUserResponse, url);
            }

            @Override
            public void onCallbackEnd() {
                super.onCallbackEnd();
                showNormalView();
            }
        });
    }

    @Click(R.id.rl_recommend)
    void clickRecommend(){
        if (CheckDoubleClick.isFastDoubleClick()) {
            return;
        }
        getRecommendTips();
    }

    /**
     * 显示推荐对话框
     */
    private void showRecommendDialog(final GetAgentRecommendUserAppTipsResponse.GetAgentRecommendUserAppTips data){
        View contentView = View.inflate(this, R.layout.activity_customer_details_recommend_dialog, null);
        final Dialog mDialog = DialogManager.showCenterAlert(this, contentView);
        TextView tv_tip_title = (TextView) contentView.findViewById(R.id.tv_tip_title);
        TextView tv_tip_title_sub = (TextView) contentView.findViewById(R.id.tv_tip_title_sub);
        TextView tv_msg = (TextView) contentView.findViewById(R.id.tv_msg);
        TextView tv_help = (TextView) contentView.findViewById(R.id.tv_help);
        TextView tv_recommend = (TextView) contentView.findViewById(R.id.tv_recommend);
        TextView tv_recommend_by_qr = (TextView) contentView.findViewById(R.id.tv_recommend_by_qr);
        tv_help.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDetailsActivity.this, GeneratedClassUtils.get(RecommendGuideActivity.class));
                startActivity(intent);
            }
        });
        tv_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                showRecommendSendDialog(data);
                sendRecommend();
            }
        });
        tv_recommend_by_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDetailsActivity.this, GeneratedClassUtils.get(ShowQRCodeActivity.class));
                intent.putExtra("url", data.getShortURL());
                startActivity(intent);
            }
        });

        tv_recommend.setEnabled(true);
        tv_recommend.setTextColor(getResources().getColor(R.color.color_333));
        if (data.getIsIntro() == 1) {//1 今日已推荐过
            tv_recommend.setEnabled(false);
            tv_recommend.setText("今日已发送");
            tv_recommend.setTextColor(getResources().getColor(R.color.color_ccc));
        }

        tv_tip_title.setText(data.getTipTitle());
        tv_tip_title_sub.setText(data.getTipTitleSub());
        tv_msg.setText(data.getTipContent());
    }

    /**
     * 显示推荐发送对话框
     */
    private void showRecommendSendDialog(GetAgentRecommendUserAppTipsResponse.GetAgentRecommendUserAppTips data){
        View contentView = View.inflate(this, R.layout.activity_customer_details_recommend_send_dialog, null);
        recommendSendDialog = DialogManager.showCenterAlert(this, contentView);
        recommendSendDialog.setCancelable(false);
        TextView tv_msg = (TextView) contentView.findViewById(R.id.tv_msg);
        ImageView iv_status = (ImageView) contentView.findViewById(R.id.iv_status);
        View rl_submit = contentView.findViewById(R.id.rl_submit);
        rl_submit.setEnabled(false);

        AnimationDrawable animationDrawable = (AnimationDrawable) iv_status.getDrawable();
        animationDrawable.start();

        StringBuilder msg = new StringBuilder();
        msg.append(data.getMsgTitle());
        msg.append("\n");
        List<String> featuresList = data.getFeaturesList();
        for (int i = 0; i < featuresList.size(); i++) {
            String tmp = featuresList.get(i);
            msg.append(tmp);
            if (i != featuresList.size() - 1) {
                msg.append("\n");
            }
        }
        tv_msg.setText(msg);

//        tv_msg.setText(Html.fromHtml(data.getFeaturesListStr()));
    }

    /**
     * 请求推荐APP提示文字
     */
    private void getRecommendTips(){
        showLoadingView();
        GetAgentRecommendUserAppTipsRequest request = new GetAgentRecommendUserAppTipsRequest();
        request.setAgentId(AppConfig.agentid);
        request.setUserId(mUserId);
        CustomerReqsAction.getAgentRecommendUserAppTips(this, request, new SaleAgentHttpListener<GetAgentRecommendUserAppTipsResponse>(this) {
            @Override
            public void onBusinessSuccess(GetAgentRecommendUserAppTipsResponse getAgentRecommendUserAppTipsResponse, String url) {
                showRecommendDialog(getAgentRecommendUserAppTipsResponse.getData());
            }
        });
    }

    /**
     * 发送推荐APP
     */
    private void sendRecommend(){
        SendAgentRecommendUserAppSmsRequest request = new SendAgentRecommendUserAppSmsRequest();
        request.setAgentId(AppConfig.agentid);
        request.setUserId(mUserId);
        CustomerReqsAction.sendAgentRecommendUserAppSms(this, request, new SaleAgentHttpListener<Response>(this) {

            @Override
            public void onBusinessSuccess(Response response, String url) {
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        sendSuccess();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(tt, 2000);
            }

            @Override
            public void onError(Exception exception, String url) {
                super.onError(exception, url);
                recommendSendDialog.dismiss();
            }

            @Override
            public void onBusinessFail(Response response, String url) {
                super.onBusinessFail(response, url);
                recommendSendDialog.dismiss();
            }
        });
    }

    @UiThread
    void sendSuccess(){
        tv_recommend_status.setText("已推荐，未安装 >");
        Toast.makeText(CustomerDetailsActivity.this, "发送完成", Toast.LENGTH_SHORT).show();
        recommendSendDialog.dismiss();
    }

}
