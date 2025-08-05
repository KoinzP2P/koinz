/*
 * This file is part of KOINZ.
 *
 * KOINZ is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * KOINZ is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with KOINZ. If not, see <http://www.gnu.org/licenses/>.
 */

package koinz.desktop.main.dao.economy.supply.dao;

import koinz.desktop.components.chart.ChartViewModel;

import koinz.core.locale.GlobalSettings;
import koinz.core.util.coin.BsqFormatter;

import javax.inject.Inject;

import javafx.scene.chart.XYChart;

import javafx.util.StringConverter;

import java.text.DecimalFormat;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DaoChartViewModel extends ChartViewModel<DaoChartDataModel> {
    private final DecimalFormat priceFormat;
    private final BsqFormatter bsqFormatter;


    @Inject
    public DaoChartViewModel(DaoChartDataModel dataModel, BsqFormatter bsqFormatter) {
        super(dataModel);

        this.bsqFormatter = bsqFormatter;
        priceFormat = (DecimalFormat) DecimalFormat.getNumberInstance(GlobalSettings.getLocale());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Chart data
    ///////////////////////////////////////////////////////////////////////////////////////////

    CompletableFuture<List<XYChart.Data<Number, Number>>> getTotalSupplyChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getTotalSupplyByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getSupplyChangeChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getSupplyChangeByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getTotalTradeFeesChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getTotalTradeFeesByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getProofOfBurnFromBtcFeesChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getProofOfBurnFromBtcFeesByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getProofOfBurnFromArbitrationChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getProofOfBurnFromArbitrationByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getArbitrationDiffByInterval() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getArbitrationDiffByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getTotalIssuedChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getTotalIssuedByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getCompensationChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getCompensationByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getReimbursementChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getReimbursementByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getTotalBurnedChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getTotalBurnedByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getBsqTradeFeeChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getBsqTradeFeeByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getProofOfBurnChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getProofOfBurnByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getReimbursementAfterTaggingChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getReimbursementAfterTaggingByInterval()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getBsqTradeFeeAfterTaggingChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getBsqTradeFeeByIntervalAfterTagging()));
    }

    CompletableFuture<List<XYChart.Data<Number, Number>>> getMiscBurnChartData() {
        return CompletableFuture.supplyAsync(() -> toChartData(dataModel.getMiscBurnByInterval()));
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Formatters/Converters
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected StringConverter<Number> getYAxisStringConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Number value) {
                return priceFormat.format(Double.parseDouble(bsqFormatter.formatBSQSatoshis(value.longValue()))) + " BSQ";
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        };
    }

    @Override
    protected String getTooltipValueConverter(Number value) {
        return bsqFormatter.formatBSQSatoshisWithCode(value.longValue());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // DaoChartDataModel delegates
    ///////////////////////////////////////////////////////////////////////////////////////////

    CompletableFuture<Long> getCompensationAmount() {
        return CompletableFuture.supplyAsync(dataModel::getCompensationAmount);
    }

    CompletableFuture<Long> getReimbursementAmount() {
        return CompletableFuture.supplyAsync(dataModel::getReimbursementAmount);
    }

    CompletableFuture<Long> getBsqTradeFeeAmount() {
        return CompletableFuture.supplyAsync(dataModel::getBsqTradeFeeAmount);
    }

    CompletableFuture<Long> getProofOfBurnAmount() {
        return CompletableFuture.supplyAsync(dataModel::getProofOfBurnAmount);
    }
}
