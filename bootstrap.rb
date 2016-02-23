#this file run as from Vagrant File
#this what makes it all tick

def flatten_hashmap(input)
  Hash[*input.map { |key, value| 
    value.is_a?(Hash) ? 
    flatten_hashmap(value).map { |nested_key, nested_value|
        ["#{key}_#{nested_key}",nested_value] } : [key, value] 
  }.flatten]
end

def dump_config_as_bashrc(input, outfile)
  flattened_properties = flatten_hashmap(input)

  File.open(outfile, 'w') do |fd|  
    fd.puts "# DO NOT EDIT - AUTO GENERATED FROM setup.conf\n\n"

    flattened_properties.each_pair do |key, value|
      fd.puts "cfg_#{key}=\"#{value}\""
    end
  end
end

# load the config 
$config = eval(IO.read(File.join(File.dirname(__FILE__), "setup.conf")))
$vg_config = flatten_hashmap($config)

# generate bash config file
dump_config_as_bashrc($vg_config, File.join(File.dirname(__FILE__),"setup.rc"))

#create dir if not exists
#Dir.mkdir($vg_config['code_dir']) unless File.exists?($vg_config['code_dir'])




