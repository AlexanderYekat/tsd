package ru.ttmf.mark.common;

public class DataMatrixHelpers {
    public static void splitStr(DataMatrix dataMatrix, String data, int delimiter) {

        if (data.length() > 2) {

            boolean k240 = false;

            if ((int) data.charAt(0) == delimiter) {

                data = data.substring(1);
                splitStr(dataMatrix, data, delimiter);

            } else {

                int aiLength = dataMatrix.getAILenght(
                        String.valueOf(data.charAt(0)) + data.charAt(1));

                if (aiLength == 0) {
                    aiLength = dataMatrix.getAILenght(
                            String.valueOf(data.charAt(0)) + data.charAt(1) + data.charAt(2));
                    k240 = true;
                }

                if (data.length() < aiLength)
                    aiLength = data.length() - 2;

                if (data.length() == aiLength)
                    aiLength = data.length() - 2;

                String ver = "";

                if (!k240) {
                    for (int i = 2; i <= aiLength + 1; i++) {
                        if ((int) data.charAt(i) == delimiter) {
                            aiLength = i - 1;
                            break;
                        }
                        ver = ver.concat(String.valueOf(data.charAt(i)));
                    }
                } else {
                    for (int i = 3; i <= aiLength + 2; i++) {
                        if ((int) data.charAt(i) == delimiter) {
                            aiLength = i - 1;
                            break;
                        }
                        ver = ver.concat(String.valueOf(data.charAt(i)));
                    }
                }

                if (!k240)
                    dataMatrix.setDM(String.valueOf(data.charAt(0)) + data.charAt(1), ver);
                else
                    dataMatrix.setDM(String.valueOf(data.charAt(0))
                            + data.charAt(1) + data.charAt(2), ver);

                /*if ((!dataMatrix.SGTIN_v))
                    dataMatrix.SGTIN_check();*/

                if (!k240) {

                    for (int k = aiLength + 2; k > 0; k--)
                        if (data.length() > 0)
                            data = data.substring(1);

                    splitStr(dataMatrix, data, delimiter);

                } else {

                    for (int k = aiLength + 3; k > 0; k--)
                        if (data.length() > 0)
                            data = data.substring(1);

                    splitStr(dataMatrix, data, delimiter);
                }
            }
        }
    }
}
