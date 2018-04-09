package br.edu.uepb.nutes.haniot;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.List;

import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.BodyCompositionAdapter;
import br.edu.uepb.nutes.haniot.devices.hdp.BodyCompositionHDPActivity;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.server.historical.CallbackHistorical;
import br.edu.uepb.nutes.haniot.server.historical.Historical;
import br.edu.uepb.nutes.haniot.server.historical.HistoricalType;
import br.edu.uepb.nutes.haniot.server.historical.Params;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class US07_AC02_TC02 {


    // Body Composition Test
    BodyCompositionAdapter mAdapter;
    boolean itShouldLoadMore;

    int c = 0;
    @Before
    public void setUp() throws Exception {

        String IdNutes = "5a7d16f148ef3200140161d7";


// Processo para acessar atributos privados

        Field privateStringField = BodyCompositionHDPActivity.class.  // What is this field Class?????
                getDeclaredField("mAdapter");

        privateStringField.setAccessible(true);

        mAdapter = (BodyCompositionAdapter) privateStringField.get(rule.getActivity());

        privateStringField = BodyCompositionHDPActivity.class.  // What is this field Class?????
                getDeclaredField("itShouldLoadMore");

        privateStringField.setAccessible(true);

        itShouldLoadMore = (boolean) privateStringField.get(rule.getActivity());


//        Field sessionField = BloodPressureHDPActivity.class.  // What is this field Class?????
//                getDeclaredField("session");
//
//        privateStringField.setAccessible(true);
//
//        Session session = (Session) sessionField.get(rule.getActivity());
//

        // fieldValue.getNotSent(Long.parseLong(session.get_idLogged())).get(0).getDevice().getTarget().getName();

    }

    @Rule
    public ActivityTestRule<BodyCompositionHDPActivity> rule = new ActivityTestRule<BodyCompositionHDPActivity>(BodyCompositionHDPActivity.class);
    // Criando lista de medições
    List<Measurement> todaasMedicoes;

    @Test
    public void Teste() throws Exception {

        Session session = new Session(rule.getActivity());

        Params params = new Params(session.get_idLogged(), MeasurementType.BODY_MASS);
        Historical historical = new Historical.Query()
                .type(HistoricalType.MEASUREMENTS_TYPE_USER)
                .params(params) // Measurements of the blood glucose type, associated to the user
                .pagination(0, Integer.MAX_VALUE)
                .build();

        historical.request(rule.getActivity(), new CallbackHistorical<Measurement>() {

            @Override
            public void onBeforeSend() {

            }

            @Override
            public void onError(JSONObject result) {

            }

            @Override
            public void onResult(List<Measurement> result) {
                todaasMedicoes = result;

            }

            @Override
            public void onAfterSend() {

            }

        });

        // mAdapter.getItems();

        waitLoad();
        do{

            loadData();

            if(c == todaasMedicoes.size()){
                break;
            } else {
                onView(withId(R.id.data_swiperefresh))
                        .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));
            }
        } while (c == todaasMedicoes.size());
        // Condição de parada até aparecer "No more" , já que a aplicação está sendo carregada e mostrando 20 vezes




    }

    public void loadData(){

        c = 0;
        for (Measurement m : mAdapter.getItems()){

            assertEquals(m, todaasMedicoes.get(c) );
            c++;
        }

    }



    public void waitLoad(){

        while (todaasMedicoes == null){



        }




    }
    public static ViewAction withCustomConstraints(final ViewAction action, final Matcher<View> constraints) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return constraints;
            }

            @Override
            public String getDescription() {
                return action.getDescription();
            }

            @Override
            public void perform(UiController uiController, View view) {
                action.perform(uiController, view);
            }
        };
    }
}
