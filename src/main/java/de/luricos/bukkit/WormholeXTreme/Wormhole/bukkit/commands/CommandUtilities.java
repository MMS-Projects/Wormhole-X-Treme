/*
 * Wormhole X-Treme Plugin for Bukkit
 * Copyright (C) 2011 Lycano <https://github.com/lycano/Wormhole-X-Treme/>
 *
 * Copyright (C) 2011 Ben Echols
 *                    Dean Bailey
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.luricos.bukkit.WormholeXTreme.Wormhole.bukkit.commands;

import de.luricos.bukkit.WormholeXTreme.Wormhole.model.Stargate;
import de.luricos.bukkit.WormholeXTreme.Wormhole.model.StargateManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * WormholeXTreme Commands and command specific methods.
 * 
 * @author Dean Bailey (alron)
 * @author Ben Echols (Lologarithm)
 */
public class CommandUtilities {

	/**
     * Close gate.
     * 
     * @param stargate
     *            the stargate
     * @param iris
     *            the iris
     */
    public static void closeGate(final Stargate stargate, final boolean iris) {
        if (stargate != null) {
            if (stargate.isGateActive()) {
                stargate.shutdownStargate(true);
                if (stargate.isGateActive()) {
                    stargate.setGateActive(false);
                }
            }
            if (stargate.isGateLightsActive()) {
                stargate.lightStargate(false);
                stargate.stopActivationTimer();
            }
            if (iris && stargate.isGateIrisActive()) {
                stargate.toggleIrisActive(false);
            }
        }
    }
    
    /**
     * Toggle iris.
     * 
     * @param stargate
     *            the stargate
     */
    public static boolean toggleIris(final Stargate stargate) {
        if (stargate != null) {
            stargate.toggleIrisActive(false);
            return stargate.isGateIrisActive();
        }
        return false;
    }

    /**
     * Command escaper.
     * Checks for " and escapes it.
     * 
     * @param args The String[] argument list to escape quotes on.
     * @return String[] with properly escaped quotes.
     */
    public static String[] commandEscaper(final String[] args) {
        StringBuilder tempString = new StringBuilder();
        boolean startQuoteFound = false;
        boolean endQuoteFound = false;

        final ArrayList<String> argsPartsList = new ArrayList<String>();

        for (final String part : args) {
            // First check to see if we have a starting or stopping quote
            if (part.contains("\"") && !startQuoteFound) {
                // Two quotes in same string = no spaces in quoted text;
                if (!part.replaceFirst("\"", "").contains("\"")) {
                    startQuoteFound = true;
                }
            } else if (part.contains("\"") && startQuoteFound) {
                endQuoteFound = true;
            }

            // If no quotes yet, we just append to list
            if (!startQuoteFound) {
                argsPartsList.add(part);
            }

            // If we have quotes we should make sure to append the values
            // if we found the last quote we should stop adding.
            if (startQuoteFound) {
                tempString.append(part.replace("\"", ""));
                if (endQuoteFound) {
                    argsPartsList.add(tempString.toString());
                    startQuoteFound = false;
                    endQuoteFound = false;
                    tempString = new StringBuilder();
                } else {
                    tempString.append(" ");
                }
            }
        }
        
        return argsPartsList.toArray(new String[argsPartsList.size()]);
    }

    /**
     * Gate remove.
     * 
     * @param stargate the stargate
     * @param destroy true to destroy gate blocks
     */
    public static void gateRemove(final Stargate stargate, final boolean destroy) {
        stargate.setupGateSign(false);
        // reset teleport sign for possible recreation when not destroyed
        if (!destroy) {
            stargate.resetTeleportSign();
        }

        if (!stargate.getGateIrisDeactivationCode().equals("")) {
            if (stargate.isGateIrisActive()) {
                stargate.toggleIrisActive(false);
            }
            stargate.setupIrisLever(false);
        }
        if (stargate.isGateRedstonePowered()) {
            stargate.setupRedstone(false);
        }
        
        if (destroy) {
            stargate.deleteGateBlocks();
            stargate.deletePortalBlocks();
            stargate.deleteTeleportSign();
        }
        StargateManager.removeStargate(stargate);
    }

    /**
     * Gets the gate network.
     * 
     * @param stargate the stargate
     * @return the gate network
     */
    public static String getGateNetwork(final Stargate stargate) {
        if (stargate != null) {
            if (stargate.getGateNetwork() != null) {
                return stargate.getGateNetwork().getNetworkName();
            }
        }
        return "Public";
    }

    /**
     * Checks if is boolean.
     * 
     * @param booleanString the boolean string
     * @return true, if is boolean
     */
    public static boolean isBoolean(final String booleanString) {
        return booleanString.equalsIgnoreCase("true") || booleanString.equalsIgnoreCase("false");
    }

    /**
     * Player check.
     * 
     * @param sender the sender
     * @return true, if successful
     */
    public static boolean playerCheck(final CommandSender sender) {
        return sender instanceof Player;

    }
}
