unless Dir.exist? "#{ENV['JETTY_BASE']}/temp"
  directory "BE_tempdir_creation" do
    path "#{ENV['JETTY_BASE']}/temp"
    owner 'jetty'
    group 'jetty'
    mode '0755'
    action :create
  end
end

unless Dir.exist? "#{ENV['JETTY_BASE']}/config"
  directory "BE_create_config_dir" do
    path "#{ENV['JETTY_BASE']}/config"
    owner 'jetty'
    group 'jetty'
    mode '0755'
    action :create
  end
end

unless Dir.exist? "#{ENV['JETTY_BASE']}/config/catalog-be"
  directory "BE_create_catalog-be" do
    path "#{ENV['JETTY_BASE']}/config/catalog-be"
    owner 'jetty'
    group 'jetty'
    mode '0755'
    action :create
  end
end
