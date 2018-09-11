# Monkey patch for network interface detection bug in Vagrant 1.8.6
# https://github.com/mitchellh/vagrant/issues/7876
#
# This file is a Derivative Work of Vagrant source, covered by the MIT license.

require Vagrant.source_root.join('plugins/guests/linux/cap/network_interfaces.rb')

module VagrantPlugins
  module GuestLinux
    module Cap
      class NetworkInterfaces
        # Valid ethernet device prefix values.
        # eth - classic prefix
        # en  - predictable interface names prefix
        POSSIBLE_ETHERNET_PREFIXES = ["eth".freeze, "en".freeze].freeze

        @@logger = Log4r::Logger.new("vagrant::guest::linux::network_interfaces")

        # Get network interfaces as a list. The result will be something like:
        #
        #   eth0\nenp0s8\nenp0s9
        #
        # @return [Array<String>]
        def self.network_interfaces(machine, path = "/sbin/ip")
          s = ""
          machine.communicate.sudo("#{path} -o -0 addr | grep -v LOOPBACK | awk '{print $2}' | sed 's/://'") do |type, data|
            s << data if type == :stdout
          end
          ifaces = s.split("\n")
          @@logger.debug("Unsorted list: #{ifaces.inspect}")
          # Break out integers from strings and sort the arrays to provide
          # a natural sort for the interface names
          ifaces = ifaces.map do |iface|
            iface.scan(/(.+?)(\d+)/).flatten.map do |iface_part|
              if iface_part.to_i.to_s == iface_part
                iface_part.to_i
              else
                iface_part
              end
            end
          end.sort.map(&:join)
          @@logger.debug("Sorted list: #{ifaces.inspect}")
          # Extract ethernet devices and place at start of list
          resorted_ifaces = []
          resorted_ifaces += ifaces.find_all do |iface|
            POSSIBLE_ETHERNET_PREFIXES.any?{|prefix| iface.start_with?(prefix)} &&
              !iface.include?(':')
          end
          resorted_ifaces += ifaces - resorted_ifaces
          ifaces = resorted_ifaces
          @@logger.debug("Ethernet preferred sorted list: #{ifaces.inspect}")
          ifaces
        end
      end
    end
  end
end

Vagrant::UI::Colored.new.info 'Vagrant Patch Loaded: GuestLinux network_interfaces (1.8.6)'
