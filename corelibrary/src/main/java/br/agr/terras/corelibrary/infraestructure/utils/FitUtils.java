package br.agr.terras.corelibrary.infraestructure.utils;

import com.garmin.fit.BatteryStatus;
import com.garmin.fit.Decode;
import com.garmin.fit.DeveloperField;
import com.garmin.fit.DeveloperFieldDescription;
import com.garmin.fit.DeveloperFieldDescriptionListener;
import com.garmin.fit.DeviceInfoMesg;
import com.garmin.fit.DeviceInfoMesgListener;
import com.garmin.fit.Factory;
import com.garmin.fit.Field;
import com.garmin.fit.FieldBase;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.FileIdMesgListener;
import com.garmin.fit.FitRuntimeException;
import com.garmin.fit.Gender;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.MonitoringMesg;
import com.garmin.fit.MonitoringMesgListener;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.RecordMesgListener;
import com.garmin.fit.UserProfileMesg;
import com.garmin.fit.UserProfileMesgListener;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import br.agr.terras.corelibrary.infraestructure.shapefile.GeoErroListener;
import br.agr.terras.corelibrary.infraestructure.shapefile.GpxParser;

/**
 * Created by leo on 13/12/16.
 */

public class FitUtils {
    private Decode decode;
    private boolean error;

    public FitUtils(File file, GeoErroListener errorListener) {
        decode = new Decode();
        //decode.skipHeader();        // Use on streams with no header and footer (stream contains FIT defn and data messages only)
        //decode.incompleteStream();  // This suppresses exceptions with unexpected eof (also incorrect crc)
        MesgBroadcaster mesgBroadcaster = new MesgBroadcaster( decode );
        Listener listener = new Listener();
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            errorListener.erroEncontrarArquivo();
        }
        try {
            if ( !decode.checkFileIntegrity(in ) ) {
                throw new RuntimeException( "FIT file integrity failed." );
            }
        } catch ( RuntimeException e ) {
            errorListener.erroInterpretarArquivo();
            System.err.print( "Exception Checking File Integrity: " );
            System.err.println( e.getMessage() );
            System.err.println( "Trying to continue..." );
        } finally {
            try {
                in.close();
            } catch ( java.io.IOException e ) {
                errorListener.erroEncontrarArquivo();
                throw new RuntimeException( e );
            }
        }
        mesgBroadcaster.addListener( (FileIdMesgListener) listener );
        mesgBroadcaster.addListener( (UserProfileMesgListener) listener );
        mesgBroadcaster.addListener( (DeviceInfoMesgListener) listener );
        mesgBroadcaster.addListener( (MonitoringMesgListener) listener );
        mesgBroadcaster.addListener( (RecordMesgListener) listener );

        decode.addListener( (DeveloperFieldDescriptionListener) listener );

        try {
            decode.read( in, mesgBroadcaster, mesgBroadcaster );
        } catch ( FitRuntimeException e ) {
            errorListener.erroInterpretarArquivo();
            // If a file with 0 data size in it's header  has been encountered,
            // attempt to keep processing the file
            if ( decode.getInvalidFileDataSize() ) {
                decode.nextFile();
                decode.read( in, mesgBroadcaster, mesgBroadcaster );
            } else {
                System.err.print( "Exception decoding file: " );
                System.err.println( e.getMessage() );

                try {
                    in.close();
                } catch ( java.io.IOException f ) {
                    errorListener.erroInterpretarArquivo();
                    throw new RuntimeException( f );
                }

                return;
            }
        }

        try {
            in.close();
        } catch ( java.io.IOException e ) {
            errorListener.erroInterpretarArquivo();
            throw new RuntimeException( e );
        }

        System.out.println( "Decoded FIT file " + file.getName() + "." );
    }

    public boolean isError(){
        return error;
    }

    public List<LatLng> getLatLngs() {
        final List<LatLng> pontos = new ArrayList<>();
        return pontos;
    }

    public List<GpxParser.TrkPt> getPontos() {
        return null;
    }

    private static class Listener implements FileIdMesgListener, UserProfileMesgListener, DeviceInfoMesgListener, MonitoringMesgListener, RecordMesgListener, DeveloperFieldDescriptionListener {

        @Override
        public void onMesg( FileIdMesg mesg ) {
            System.out.println( "File ID:" );

            if ( mesg.getType() != null ) {
                System.out.print( "   Type: " );
                System.out.println( mesg.getType().getValue() );
            }

            if ( mesg.getManufacturer() != null ) {
                System.out.print( "   Manufacturer: " );
                System.out.println( mesg.getManufacturer() );
            }

            if ( mesg.getProduct() != null ) {
                System.out.print( "   Product: " );
                System.out.println( mesg.getProduct() );
            }

            if ( mesg.getSerialNumber() != null ) {
                System.out.print( "   Serial Number: " );
                System.out.println( mesg.getSerialNumber() );
            }

            if ( mesg.getNumber() != null ) {
                System.out.print( "   Number: " );
                System.out.println( mesg.getNumber() );
            }
        }

        @Override
        public void onMesg( UserProfileMesg mesg ) {
            System.out.println( "User profile:" );

            if ( ( mesg.getFriendlyName() != null ) ) {
                System.out.print( "   Friendly Name: " );
                System.out.println( mesg.getFriendlyName() );
            }

            if ( mesg.getGender() != null ) {
                if ( mesg.getGender() == Gender.MALE ) {
                    System.out.println( "   Gender: Male" );
                } else if ( mesg.getGender() == Gender.FEMALE ) {
                    System.out.println( "   Gender: Female" );
                }
            }

            if ( mesg.getAge() != null ) {
                System.out.print( "   Age [years]: " );
                System.out.println( mesg.getAge() );
            }

            if ( mesg.getWeight() != null ) {
                System.out.print( "   Weight [kg]: " );
                System.out.println( mesg.getWeight() );
            }
        }

        @Override
        public void onMesg( DeviceInfoMesg mesg ) {
            System.out.println( "Device info:" );

            if ( mesg.getTimestamp() != null ) {
                System.out.print( "   Timestamp: " );
                System.out.println( mesg.getTimestamp() );
            }

            if ( mesg.getBatteryStatus() != null ) {
                System.out.print( "   Battery status: " );

                switch ( mesg.getBatteryStatus() ) {

                    case BatteryStatus.CRITICAL:
                        System.out.println( "Critical" );
                        break;
                    case BatteryStatus.GOOD:
                        System.out.println( "Good" );
                        break;
                    case BatteryStatus.LOW:
                        System.out.println( "Low" );
                        break;
                    case BatteryStatus.NEW:
                        System.out.println( "New" );
                        break;
                    case BatteryStatus.OK:
                        System.out.println( "OK" );
                        break;
                    default:
                        System.out.println( "Invalid" );
                }
            }
        }

        @Override
        public void onMesg( MonitoringMesg mesg ) {
            System.out.println( "Monitoring:" );

            if ( mesg.getTimestamp() != null ) {
                System.out.print( "   Timestamp: " );
                System.out.println( mesg.getTimestamp() );
            }

            if ( mesg.getActivityType() != null ) {
                System.out.print( "   Activity Type: " );
                System.out.println( mesg.getActivityType() );
            }

            // Depending on the ActivityType, there may be Steps, Strokes, or Cycles present in the file
            if ( mesg.getSteps() != null ) {
                System.out.print( "   Steps: " );
                System.out.println( mesg.getSteps() );
            } else if ( mesg.getStrokes() != null ) {
                System.out.print( "   Strokes: " );
                System.out.println( mesg.getStrokes() );
            } else if ( mesg.getCycles() != null ) {
                System.out.print( "   Cycles: " );
                System.out.println( mesg.getCycles() );
            }

            printDeveloperData( mesg );
        }

        @Override
        public void onMesg( RecordMesg mesg ) {
            System.out.println( "Record:" );

            printValues( mesg, RecordMesg.HeartRateFieldNum );
            printValues( mesg, RecordMesg.CadenceFieldNum );
            printValues( mesg, RecordMesg.DistanceFieldNum );
            printValues( mesg, RecordMesg.SpeedFieldNum );

            printDeveloperData( mesg );
        }

        private void printDeveloperData( Mesg mesg ) {
            for ( DeveloperField field : mesg.getDeveloperFields() ) {
                if ( field.getNumValues() < 1 ) {
                    continue;
                }

                if ( field.isDefined() ) {
                    System.out.print( "   " + field.getName() );

                    if ( field.getUnits() != null ) {
                        System.out.print( " [" + field.getUnits() + "]" );
                    }

                    System.out.print( ": " );
                } else {
                    System.out.print( "   Undefined Field: " );
                }

                System.out.print( field.getValue( 0 ) );
                for ( int i = 1; i < field.getNumValues(); i++ ) {
                    System.out.print( "," + field.getValue( i ) );
                }

                System.out.println();
            }
        }

        @Override
        public void onDescription( DeveloperFieldDescription desc ) {
            System.out.println( "New Developer Field Description" );
            System.out.println( "   App Id: " + desc.getApplicationId() );
            System.out.println( "   App Version: " + desc.getApplicationVersion() );
            System.out.println( "   Field Num: " + desc.getFieldDefinitionNumber() );
        }

        private void printValues(Mesg mesg, int fieldNum ) {
            Iterable<FieldBase> fields = mesg.getOverrideField( (short) fieldNum );
            Field profileField = Factory.createField( mesg.getNum(), fieldNum );
            boolean namePrinted = false;

            if ( profileField == null ) {
                return;
            }

            for ( FieldBase field : fields ) {
                if ( !namePrinted ) {
                    System.out.println( "   " + profileField.getName() + ":" );
                    namePrinted = true;
                }

                if ( field instanceof Field ) {
                    System.out.println( "      native: " + field.getValue() );
                } else {
                    System.out.println( "      override: " + field.getValue() );
                }
            }
        }
    }
}
