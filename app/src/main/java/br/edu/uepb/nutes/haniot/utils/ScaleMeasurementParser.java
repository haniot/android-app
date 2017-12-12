package br.edu.uepb.nutes.haniot.utils;

import br.edu.uepb.nutes.haniot.model.MeasurementScale;

/**
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ScaleMeasurementParser {

    public static MeasurementScale parse(final byte[] data) {
        MeasurementScale measurementScale = new MeasurementScale();

        if (data != null && data.length > 0) {
            measurementScale.setRegistrationTime(DateUtils.getCurrentDatetime());

            /**
             * 03: response type
             *     01 - unfinished weighing
             *     02 - finished weighing
             */
            boolean isFinalized = String.format("%02X", data[3]).equals("02");
            measurementScale.setFinalized(isFinalized);

            if (!isFinalized) {
                /**
                 * unfinished weighing
                 * 08-09: weight - BE uint16 times 0.01
                 */
                measurementScale.setWeight(Integer.valueOf(String.format("%02X", data[8]) + String.format("%02X", data[9]), 16) * 0.01f);

                return measurementScale;
            }

            /**
             * 09-12: userID - BE uint32
             */
            measurementScale.setUserIdDevice(Integer.valueOf(String.format("%02X", data[9]) + String.format("%02X", data[10])
                    + String.format("%02X", data[11]) + String.format("%02X", data[12]), 16));

            /**
             * finished weighing
             * 13-14: weight - BE uint16 times 0.01
             */
            measurementScale.setWeight(Integer.valueOf(String.format("%02X", data[13]) + String.format("%02X", data[14]), 16) * 0.01f);

            measurementScale.setResistence(Integer.valueOf(String.format("%02X", data[15]) + String.format("%02X", data[16]), 16));
            measurementScale.setBodyFat(Integer.valueOf(String.format("%02X", data[17]) + String.format("%02X", data[18]), 16) * 0.01f);

        }

        return measurementScale;
    }
}

/**
 * pwnall/yunmai_protocol.txt
 * https://gist.github.com/pwnall/4ec3cc3d18affa062dd5596f1b4308c9
 * <p>
 * Yunmai smart scale (M1301, M1302, M1303) Bluetooth LE protocol notes
 * <p>
 * Commands are written to GATT attribute 0xffe9 of service 0xffe5. Responses come
 * as value change notifications for GATT attribute 0xffe4 of service 0xffe0. These
 * are 16-bit Bluetooth LE UUIDs, so nnnn is 0000nnnn-0000-1000-8000-00805F9B34FB.
 * <p>
 * -----
 * <p>
 * Packet Structure
 * <p>
 * 0d - packet start
 * 1e - scale software version (only in responses)
 * nn - total packet length (includes packet start and CHK)
 * nn - command / response opcode
 * <p>
 * data
 * <p>
 * CHK - XOR of all bytes except for frame start
 * <p>
 * Example command: 0d 05 13 00 16
 * Example response: 0d 1e 05 05 1e
 * <p>
 * -------
 * <p>
 * Response
 * <p>
 * BE = big endian (most numbers are big endian)
 * LE = little endian (the weight stored in a profile is little endian)
 * <p>
 * 00: 0d - packet start
 * 01: 1e - scale software version
 * 02: total packet length (includes packet start and CHK)
 * 03: response type
 * <p>
 * 03: 01 - unfinished weighing
 * 04-07: date (unix time, seconds) - BE uint32
 * 08-09: weight - BE uint16 times 0.01
 * 10: CHK
 * <p>
 * 03: 02 - finished weighing
 * 04: 00 - historical info
 * 05-08: date (unix time, seconds) - BE uint32
 * 09-12: recognized userID - BE uint32
 * 13-14: weight - BE uint16 times 0.01
 * 15-16: resistance - BE uint 16
 * 17-18: fat percentage - BE uint16 times 0.01
 * 19: CHK
 * <p>
 * 03: 06 - result to user operation
 * 04: operation type - USER_ADD_OR_UPDATE: 1 | USER_ADD_OR_QUERY: 3 | USER_DELETE: 2
 * 05-08: userID - 4 bytes, BE
 * 09: height - in cm
 * 10: sex - 1 for male
 * 11: age - in years
 * 12: waist line - default 85 (0x55)
 * 13: bust - default 90 (0x5a)
 * 14-15: basisWeight - default 0, set to previously received weight - LE uint16 times 0.01
 * 16: display unit - 1 metric
 * <p>
 * 03: 17 - device time
 * 04-07:
 * 08: CHK
 * <p>
 * <p>
 * -----
 * <p>
 * Command
 * <p>
 * 00: 0d - packet start
 * 01: total packet length (includes packet start and CHK)
 * 02: command
 * <p>
 * 02: 11 - set time
 * 03-07: date (unix time, seconds) - BE uint32
 * 08: fractional second
 * 09: CHK
 * <p>
 * 02: 17 - read time
 * 03: CHK
 * <p>
 * 02: 10 - user operation
 * 03: operation type - USER_ADD_OR_UPDATE: 1 | USER_ADD_OR_QUERY: 3 | USER_DELETE: 2
 * 04-07: userID - 4 bytes, BE
 * 08: height - in cm
 * 09: sex - 1 for male, 2 for female
 * 10: age - in years
 * 11: waist line - default 85 (0x55)
 * 12: bust - default 90 (0x5a)
 * 13-14: basisWeight - default 0, set to previously received weight
 * 15: display unit - 1 for metric, 2 for imperial
 * 16: body type - always 3
 * 17: CHK
 **/