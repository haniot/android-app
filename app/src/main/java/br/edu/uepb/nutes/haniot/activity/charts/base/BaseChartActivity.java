package br.edu.uepb.nutes.haniot.activity.charts.base;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.mikephil.charting.charts.Chart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.type.MeasurementType;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Base Chart implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
abstract public class BaseChartActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "ChartActivity";

    public final int CHART_TYPE_DAY = 1;
    public final int CHART_TYPE_SEVEN = 2;
    public final int CHART_TYPE_MONTH = 3;
    public final int CHART_TYPE_YEAR = 4;

    protected int currentChartType;
    protected Repository mRepository;
    private AppPreferencesHelper appPreferencesHelper;
    protected Patient patient;

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.chart_progress_bar)
    public ProgressBar mProgressBar;

    @BindView(R.id.fab_year)
    public FloatingActionButton fabYear;

    @BindView(R.id.fab_month)
    public FloatingActionButton fabMonth;

    @BindView(R.id.fab_week)
    public FloatingActionButton fabWeek;

    @BindView(R.id.fab_day)
    public FloatingActionButton fabDay;

    @BindView(R.id.menu_period)
    public FloatingActionMenu fabActionMenu;

    @BindView(R.id.box_measurement)
    public RelativeLayout boxMeasurement;

    @BindView(R.id.box_toolbar)
    public RelativeLayout boxToolbar;

    public BaseChartActivity() {
        this.currentChartType = CHART_TYPE_MONTH;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        mRepository = Repository.getInstance(this);
        patient = appPreferencesHelper.getLastPatient();

        initView();

        if (isTablet(this)) {
            Log.i(TAG, "is tablet");
            boxMeasurement.getLayoutParams().height = 600;
            boxToolbar.getLayoutParams().height = 630;
            boxMeasurement.requestLayout();
            boxToolbar.requestLayout();
        }
        fabDay.setOnClickListener(this);
        fabWeek.setOnClickListener(this);
        fabMonth.setOnClickListener(this);
        fabYear.setOnClickListener(this);
        fabActionMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_month));
    }

    @Override
    protected void onDestroy() {
        DisposableManager.dispose();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData(currentChartType);
    }

    public abstract void initView();

    public abstract int getLayout();

    public abstract String getTypeMeasurement();

    public abstract Chart getChart();

    abstract public void onUpdateData(List<Measurement> data, int currentChartType);

    /**
     * Check if is tablet.
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_day:
                currentChartType = CHART_TYPE_DAY;
                fabActionMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_day));
                break;
            case R.id.fab_month:
                currentChartType = CHART_TYPE_MONTH;
                fabActionMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_month));
                break;
            case R.id.fab_week:
                currentChartType = CHART_TYPE_SEVEN;
                fabActionMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_week));
                break;
            case R.id.fab_year:
                currentChartType = CHART_TYPE_YEAR;
                fabActionMenu.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_year));
                break;
        }
        requestData(currentChartType);
        fabActionMenu.close(true);
    }

    protected void requestData(int type) {
        requestDataInServer(type);
    }

    /**
     * Calculates the end date of according initial date and a period of time
     *
     * @param date   Initial date
     * @param period Period of time
     * @return
     */
    private String calcStartDate(String date, int period) {
        Calendar c = DateUtils.convertStringDateToCalendar(date, DateUtils.DATE_FORMAT_DATE_TIME);
        switch (period) {
            case CHART_TYPE_DAY:
                c.add(Calendar.DAY_OF_YEAR, -1);
                break;
            case CHART_TYPE_SEVEN:
                c.add(Calendar.DAY_OF_YEAR, -7);
                break;
            case CHART_TYPE_YEAR:
                c.add(Calendar.DAY_OF_YEAR, -365);
                break;
            default:
                c.add(Calendar.DAY_OF_YEAR, -30);
                break;
        }
        return DateUtils.formatDate(c.getTimeInMillis(), DateUtils.DATE_FORMAT_DATE_TIME);
    }

    /**
     * Request data in server of according with a period of time
     *
     * @param period The period of time: 1d, 1w, 1m or 1y
     */
    protected void requestDataInServer(int period) {
        String dateEnd = DateUtils.getCurrentDateTimeUTC(); // data atual
        String dateStart = calcStartDate(dateEnd, period);
        Log.w(TAG, "Data inicio: " + dateStart);
        Log.w(TAG, "Data fim: " + dateEnd);

//        if (ConnectionUtils.internetIsEnabled(this)) {
        DisposableManager.add(mRepository.
                getAllMeasurementsByType(patient, getTypeMeasurement(), "timestamp",
                        dateStart, dateEnd, 1, 100)
                .doOnSubscribe(disposable -> {
                    Log.w(TAG, "onBeforeSend()");
                    mProgressBar.setVisibility(View.VISIBLE);
                    getChart().setVisibility(View.INVISIBLE);
                })
                .subscribe(measurements -> {
                    Log.w(TAG, "onSuccess()");
                    if (measurements != null && measurements.size() > 0) {
                        runOnUiThread(() -> {
                            onUpdateData(measurements, currentChartType);
                            createMoreInfo(measurements);
                        });
                    } else {
                        runOnUiThread(() -> {
                            onUpdateData(measurements, currentChartType);
                            createMoreInfo(measurements);
                            printMessage(getString(R.string.no_data_available));
                        });
                    }
                }, error -> {
                    Log.w(TAG, "onError()");
                    printMessage(getString(R.string.error_500));
                }));
//        } else {
//            runOnUiThread(() -> {
//                onUpdateData(new ArrayList<>(), currentChartType);
//                createMoreInfo(new ArrayList<>());
////                printMessage(getString(R.string.connect_network_try_again));
//            });
//        }
    }

    /**
     * Display message.
     *
     * @param message
     */
    private void printMessage(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    public void createMoreInfo(List<Measurement> measurements) {
        ArrayList<InfoMeasurement> infoMeasurements = new ArrayList<>();
        infoMeasurements.addAll(getInfosBase(measurements));

        GridView gridView = findViewById(R.id.moreinfo_grid);
        InfoAdapter infoAdapter = new InfoAdapter(this, infoMeasurements);
        gridView.setAdapter(infoAdapter);
    }

    protected ArrayList<InfoMeasurement> getInfosBase(List<Measurement> measurements) {
        InfoMeasurement max = new InfoMeasurement(getString(R.string.info_max), "-");
        InfoMeasurement min = new InfoMeasurement(getString(R.string.info_min), "-");
        InfoMeasurement avg = new InfoMeasurement(getString(R.string.info_avarage), "-");
        InfoMeasurement per = new InfoMeasurement(getString(R.string.info_period), " - ");

        if (measurements != null && !measurements.isEmpty()) {
            String firstMeasurementDate, lastMeasurementDate, unit = " ";
            double mMax = 0, mMin = 0, mAvg = 0.0;

            int systolicMax = 0, systolicMin = 0, diastolicMax = 0, diastolicMin = 0;

            String type = getTypeMeasurement();

            if (MeasurementType.HEART_RATE.equals(type)) {
                mMax = measurements.get(0).getDataset().get(0).getValue();
                mMin = measurements.get(0).getDataset().get(0).getValue();

                for (Measurement measurement : measurements) {
                    double value = measurement.getDataset().get(0).getValue();
                    mAvg += value;

                    if (mMax < value) mMax = value;
                    if (mMin > value) mMin = value;
                }
                firstMeasurementDate = DateUtils.formatDate(measurements.get(0).getDataset().get(0).getTimestamp(), getString(R.string.date_format));
                lastMeasurementDate = DateUtils.formatDate(measurements.get(measurements.size() - 1).getDataset().get(0).getTimestamp(), getString(R.string.date_format));
            } else if (MeasurementType.BLOOD_PRESSURE.equals(type)) {
                int mediaMax, mediaMin;

                systolicMax = measurements.get(0).getSystolic();
                systolicMin = measurements.get(0).getSystolic();
                diastolicMax = measurements.get(0).getDiastolic();
                diastolicMin = measurements.get(0).getDiastolic();

                mediaMax = calcPressureAverage(measurements.get(0).getSystolic(), measurements.get(0).getDiastolic());
                mediaMin = calcPressureAverage(measurements.get(0).getSystolic(), measurements.get(0).getDiastolic());

                for (Measurement m : measurements) {
                    int valor = calcPressureAverage(m.getSystolic(), m.getDiastolic());
                    mAvg += valor;

                    if (mediaMax < valor) {
                        mediaMax = valor;
                        systolicMax = m.getSystolic();
                        diastolicMax = m.getDiastolic();
                    }
                    if (mediaMin > valor) {
                        mediaMin = valor;
                        systolicMin = m.getSystolic();
                        diastolicMin = m.getDiastolic();
                    }
                }
                firstMeasurementDate = DateUtils.formatDate(measurements.get(0).getTimestamp(), getString(R.string.date_format));
                lastMeasurementDate = DateUtils.formatDate(measurements.get(measurements.size() - 1).getTimestamp(), getString(R.string.date_format));
            } else {
                mMax = measurements.get(0).getValue();
                mMin = measurements.get(0).getValue();

                for (Measurement measurement : measurements) {
                    double value = measurement.getValue();
                    mAvg += value;

                    if (mMax < value) mMax = value;
                    if (mMin > value) mMin = value;
                }
                firstMeasurementDate = DateUtils.formatDate(measurements.get(0).getTimestamp(), getString(R.string.date_format));
                lastMeasurementDate = DateUtils.formatDate(measurements.get(measurements.size() - 1).getTimestamp(), getString(R.string.date_format));
            }
            mAvg /= measurements.size();
            unit += measurements.get(0).getUnit();

            if (type.equals(MeasurementType.HEART_RATE) || type.equals(MeasurementType.BLOOD_GLUCOSE)) {
                max.setValue(((int) mMax) + unit);
                min.setValue(((int) mMin) + unit);
                avg.setValue(String.format("%.1f", mAvg) + unit);
            } else if (type.equals(MeasurementType.BLOOD_PRESSURE)) {
                max.setValue(systolicMax + "/" + diastolicMax + "\n" + unit);
                min.setValue(systolicMin + "/" + diastolicMin + "\n" + unit);
                avg.setValue(String.format("%.1f", mAvg));
            } else {
                max.setValue(String.format("%.1f", mMax) + unit);
                min.setValue(String.format("%.1f", mMin) + unit);
                avg.setValue(String.format("%.1f", mAvg) + unit);
            }
            per.setValue(firstMeasurementDate.equals(lastMeasurementDate) ?
                    firstMeasurementDate : firstMeasurementDate + "\n-\n" + lastMeasurementDate);
        }
        ArrayList<InfoMeasurement> infos = new ArrayList<>();
        infos.add(max);
        infos.add(min);
        infos.add(avg);
        infos.add(per);
        return infos;
    }

    private int calcPressureAverage(int sis, int dia) {
        return (sis + (dia * 2)) / 3;
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }
}