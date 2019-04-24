package br.agr.terras.materialdroid;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import br.agr.terras.materialdroid.childs.calendar.AgendaAdapter;
import br.agr.terras.materialdroid.childs.calendar.AgendaView;
import br.agr.terras.materialdroid.parents.ListView;
import br.agr.terras.materialdroid.utils.calendar.BusProvider;
import br.agr.terras.materialdroid.utils.calendar.CalendarEvent;
import br.agr.terras.materialdroid.utils.calendar.CalendarManager;
import br.agr.terras.materialdroid.utils.calendar.CalendarPickerController;
import br.agr.terras.materialdroid.utils.calendar.DefaultEventRenderer;
import br.agr.terras.materialdroid.utils.calendar.EventRenderer;
import br.agr.terras.materialdroid.utils.calendar.Events;
import br.agr.terras.materialdroid.utils.calendar.ListViewScrollTracker;

/**
 * Created by leo on 15/06/16.
 */
public class CalendarView extends FrameLayout implements ListView.OnStickyHeaderChangedListener {

    private static final String LOG_TAG = CalendarView.class.getSimpleName();

    private br.agr.terras.materialdroid.childs.calendar.CalendarView mCalendarView;
    private AgendaView mAgendaView;
    private FloatingActionButton mFloatingActionButton;
    private ImageView mArrowFloatingButton;
    private AgendaAdapter agendaAdapter;

    private int mAgendaCurrentDayTextColor, mCalendarHeaderColor, mCalendarBackgroundColor, mCalendarDayTextColor, mCalendarPastDayTextColor, mCalendarCurrentDayColor, mFabColor, mMonthTextColor;
    private CalendarPickerController mCalendarPickerController;

    private ListViewScrollTracker mAgendaListViewScrollTracker;
    private AbsListView.OnScrollListener mAgendaScrollListener = new AbsListView.OnScrollListener() {
        int mCurrentAngle;
        int mMaxAngle = 85;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int scrollY = mAgendaListViewScrollTracker.calculateScrollY(firstVisibleItem, visibleItemCount);
            if (scrollY != 0) {
                mFloatingActionButton.show(true);
                //mArrowFloatingButton.setAlpha(1);
            }
            int toAngle = scrollY / 100;
            if (toAngle > mMaxAngle) {
                toAngle = mMaxAngle;
            } else if (toAngle < -mMaxAngle) {
                toAngle = -mMaxAngle;
            }
            RotateAnimation rotate = new RotateAnimation(mCurrentAngle, toAngle, mArrowFloatingButton.getWidth() / 2, mArrowFloatingButton.getHeight() / 2);
            rotate.setFillAfter(true);
            mCurrentAngle = toAngle;
            mArrowFloatingButton.startAnimation(rotate);
        }
    };

    // region Constructors

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorOptionsView, 0, 0);
        mAgendaCurrentDayTextColor = a.getColor(R.styleable.ColorOptionsView_agendaCurrentDayTextColor, getResources().getColor(R.color.theme_primary));
        mCalendarHeaderColor = a.getColor(R.styleable.ColorOptionsView_calendarHeaderColor, getResources().getColor(R.color.theme_primary_dark));
        mCalendarBackgroundColor = a.getColor(R.styleable.ColorOptionsView_calendarColor, getResources().getColor(R.color.theme_primary));
        mCalendarDayTextColor = a.getColor(R.styleable.ColorOptionsView_calendarDayTextColor, getResources().getColor(R.color.theme_text_icons));
        mCalendarCurrentDayColor = a.getColor(R.styleable.ColorOptionsView_calendarCurrentDayTextColor, getResources().getColor(R.color.calendar_text_current_day));
        mCalendarPastDayTextColor = a.getColor(R.styleable.ColorOptionsView_calendarPastDayTextColor, getResources().getColor(R.color.theme_light_primary));
        mMonthTextColor = a.getColor(R.styleable.ColorOptionsView_calendarMonthTextColor, getResources().getColor(android.R.color.black));
        mFabColor = a.getColor(R.styleable.ColorOptionsView_fabColor, getResources().getColor(R.color.theme_accent));

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_agendacalendar, this, true);

        setAlpha(1f);
    }

    // endregion

    // region Class - View

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCalendarView = (br.agr.terras.materialdroid.childs.calendar.CalendarView) findViewById(R.id.calendar_view);
        mAgendaView = (AgendaView) findViewById(R.id.agenda_view);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mArrowFloatingButton = (ImageView) findViewById(R.id.imageViewFloatingButton);
        ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{mFabColor});
        mFloatingActionButton.setColorNormal(mFabColor);

        mCalendarView.findViewById(R.id.cal_day_names).setBackgroundColor(mCalendarHeaderColor);
        mCalendarView.findViewById(R.id.list_week).setBackgroundColor(mCalendarBackgroundColor);

        mAgendaView.getAgendaListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCalendarPickerController.onEventSelected(CalendarManager.getInstance().getEvents().get(position));
            }
        });

        /*BusProvider.getInstance().toObserverable()
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {
                        {
                            if (event instanceof Events.DayClickedEvent) {
                                mCalendarPickerController.onDaySelected(((Events.DayClickedEvent) event).getDay());
                            } else if (event instanceof Events.EventsFetched) {
                                ObjectAnimator alphaAnimation = new ObjectAnimator().ofFloat(this, "alpha", getAlpha(), 1f).setDuration(500);
                                alphaAnimation.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        final long fabAnimationDelay = 500;
                                        // Just after setting the alpha from this view to 1, we hide the fab.
                                        // It will reappear as soon as the user is scrolling the Agenda view.
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                {
                                                    mFloatingActionButton.hide(true);
                                                    //mArrowFloatingButton.setAlpha(0);
                                                    mAgendaListViewScrollTracker = new ListViewScrollTracker(mAgendaView.getAgendaListView());
                                                    mAgendaView.getAgendaListView().setOnScrollListener(mAgendaScrollListener);
                                                    mFloatingActionButton.setOnClickListener(new OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            {
                                                                mAgendaView.translateList(0);
                                                                mAgendaView.getAgendaListView().scrollToCurrentDate(CalendarManager.getInstance().getToday());
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        mFloatingActionButton.hide(true);
                                                                        //mArrowFloatingButton.setAlpha(0);

                                                                    }
                                                                },fabAnimationDelay);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }, fabAnimationDelay);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });
                                alphaAnimation.start();
                            }
                        }
                    }
                });*/
    }

    // endregion

    // region Interface - StickyListHeadersListView.OnStickyHeaderChangedListener

    @Override
    public void onStickyHeaderChanged(ListView stickyListHeadersListView, View header, int position, long headerId) {
        if (CalendarManager.getInstance().getEvents().size() > 0) {
            CalendarEvent event = CalendarManager.getInstance().getEvents().get(position);
            if (event != null) {
                mCalendarView.scrollToDate(event);
                mCalendarPickerController.onScrollToDate(event.getInstanceDay());
            }
        }
    }

    // endregion

    // region Public methods

    public void init(List<CalendarEvent> eventList, Calendar minDate, Calendar maxDate, Locale locale, CalendarPickerController calendarPickerController) {
        mCalendarPickerController = calendarPickerController;

        CalendarManager.getInstance(getContext()).buildCal(minDate, maxDate, locale);

        // Feed our views com weeks list and events
        mCalendarView.init(CalendarManager.getInstance(getContext()), mCalendarDayTextColor, mCalendarCurrentDayColor, mCalendarPastDayTextColor, mMonthTextColor);

        // Load agenda events and scroll to current day
        agendaAdapter = new AgendaAdapter(mAgendaCurrentDayTextColor);
        mAgendaView.getAgendaListView().setAdapter(agendaAdapter);
        mAgendaView.getAgendaListView().setOnStickyHeaderChangedListener(this);
        CalendarManager.getInstance().loadEvents(eventList);

        // add default event renderer
        addEventRenderer(new DefaultEventRenderer());
    }

    public void addEventRenderer(@NonNull final EventRenderer<?> renderer) {
        AgendaAdapter adapter = (AgendaAdapter) mAgendaView.getAgendaListView().getAdapter();
        adapter.addEventRenderer(renderer);
    }

    public void notifyDataSetChanged(){
        if (agendaAdapter!=null)
            agendaAdapter.notifyDataSetChanged();
    }
    // endregion
}
