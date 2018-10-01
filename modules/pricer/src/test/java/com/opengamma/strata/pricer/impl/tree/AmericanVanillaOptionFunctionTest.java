/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.impl.tree;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import org.testng.annotations.Test;

import com.opengamma.strata.pricer.impl.option.BlackScholesFormulaRepository;
import com.opengamma.strata.product.common.PutCall;

@Test

public class AmericanVanillaOptionFunctionTest {
  private static final TrinomialTree TRINOMIAL_TREE = new TrinomialTree();
  private static final BinomialTree BINOMIAL_TREE = new BinomialTree();
  
  public void test_trinomialAndBinomialTree() throws ExecutionException, InterruptedException {   
    
    // Dealing with discrete dividends and european options
    double spot = 40.;
    double strike = 40.;
    double vol = 0.3;
    double rate = 0.09;
    double expiry = 6./12.;
    double[] dividendTimes = new double[]{2./12., 5./12.};
    double div = 0.50;
    
    double pvOfDivs = 0;
    for(int i = 0; i < dividendTimes.length; ++i)
      pvOfDivs += div*Math.exp(-dividendTimes[i] * rate);
  
    //Test BS price for discrete dividends versus Hull. Expected 3.67
    double bsPrice = BlackScholesFormulaRepository.price(spot - pvOfDivs, strike, expiry, vol, rate, rate, true);
    System.out.println("European Call with Discrete Dividends Benchmarked Versus Hull: " + bsPrice);
    
    //American options using trees.
    spot = 30.;
    strike = 30.;
    vol = 0.3;
    rate = 0.05;
    expiry = 0.4167;
    int steps = 100;
  
    //Test trinomial tree price for discrete dividends versus External XLS. Expected 2.047
    LatticeSpecification lattice = new CoxRossRubinsteinLatticeSpecification();
    OptionFunction function = AmericanVanillaOptionFunction.of(strike, expiry, PutCall.PUT, steps);
    double computed = TRINOMIAL_TREE.optionPrice(function, lattice, spot, vol, rate, 0.);
    System.out.println("American Put Option Using a Trinomial Tree with No Dividends Benchmarked Versus External XLS: " + computed);
    
    //American options using binomial tree and discrete dividends versus Hull. Expected 4.44.
    spot = 52.;
    strike = 50.;
    vol = 0.4;
    rate = 0.10;
    expiry = 5./12.;
    dividendTimes = new double[]{3.5/12.};
    double[] divs = new double[]{2.06};
    steps = 5;
    function = AmericanVanillaOptionFunction.of(strike, expiry, PutCall.PUT, steps);
    computed = BinomialTree.optionPrice(function, lattice, spot, vol, rate, divs, dividendTimes);
    System.out.println("American Put Option Using a Binomial Tree and a Discrete Dividend Benchmarked Versus Hull: " + computed);
  
    //American options using binomial tree and discrete dividends versus Rouah. Expected 4.5870.
    spot = 30.;
    strike = 30.;
    vol = 0.3;
    rate = 0.05;
    expiry = 0.4167;
    dividendTimes = new double[]{0.280, 0.3, 0.35, 0.4};
    divs = new double[]{1.06, 1.25, 1.10, 1.15};
    steps = 50;
    function = AmericanVanillaOptionFunction.of(strike, expiry, PutCall.PUT, steps);
    computed = BinomialTree.optionPrice(function, lattice, spot, vol, rate, divs, dividendTimes);
    System.out.println("American Put Option Using a Binomial Tree and 4 Discrete Dividends Benchmarked Versus Rouah: " + computed);
  
    //American options using binomial tree and discrete dividends versus Rouah. Expected 11.1216.
    dividendTimes = new double[]{0.025, 0.075, 0.1, 0.15, 0.18, 0.240, 0.280, 0.3, 0.35, 0.4};
    divs = new double[]{1.06, 1.25, 1.10, 1.15, 1.35, 1.03, 1.10, 1.12, 1.45, 1.23};
    steps = 100;
    function = AmericanVanillaOptionFunction.of(strike, expiry, PutCall.PUT, steps);
    computed = BinomialTree.optionPrice(function, lattice, spot, vol, rate, divs, dividendTimes);
    System.out.println("American Put Option Using a Binomial Tree and 10 Discrete Dividends Benchmarked Versus Rouah: " + computed);
  
    //Benchmark time for 1000 step granularity and portfolio of 100 options. Looking at about 4 seconds. This includes greek calculation. Much faster if just require price.
    steps = 1000;
    function = AmericanVanillaOptionFunction.of(strike, expiry, PutCall.PUT, steps);
    long start = System.currentTimeMillis();
    OptionFunction finalFunction = function;
    double finalSpot = spot;
    double finalVol = vol;
    double finalRate = rate;
    double[] finalDivs = divs;
    double[] finalDividendTimes = dividendTimes;
    new ForkJoinPool(100).submit(() -> 
    IntStream.range(0, 100).parallel()
        .forEach(number -> BinomialTree.optionPriceAdjoint(
            finalFunction,
            lattice,
            finalSpot,
            finalVol,
            finalRate,
            finalDivs,
            finalDividendTimes))).get();
    long end = System.currentTimeMillis();
    System.out.println("Running 100 American Options in Parallel with Greek Calculations Costs " + (end - start) + "ms");
  }
}
